package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.repository.ParametreRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ParametreService {

    public static final String DEFAULT_LOGO_URL = "/assets/752692.png";
    public static final String DEFAULT_APP_NOM = "HellMetz";

    private static final Set<String> EXTENSIONS_AUTORISEES = Set.of(".png", ".jpg", ".jpeg", ".gif", ".webp", ".ico", ".svg");
    private static final Set<String> CONTENT_TYPES_AUTORISES = Set.of(
            "image/png", "image/jpeg", "image/gif", "image/webp",
            "image/x-icon", "image/vnd.microsoft.icon", "image/svg+xml"
    );

    private final ParametreRepository parametreRepository;

    public ParametreService(ParametreRepository parametreRepository) {this.parametreRepository = parametreRepository;}

    @Transactional(readOnly = true)
    public List<Parametre> findAll() {return parametreRepository.findAll();}

    @Transactional(readOnly = true)
    public Parametre findByCode(String code) {return parametreRepository.findByCodeParametre(code);}

    @Transactional(readOnly = true)
    public Parametre findById(Long id) {return parametreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Parametre introuvable : " + id));
    }

    public Parametre save(Parametre parametre) {return parametreRepository.save(parametre);}

    @Transactional(readOnly = true)
    public boolean hasCustomLogo() {
        return resolveCustomLogoUrl() != null;
    }

    @Transactional(readOnly = true)
    public String getLogoUrl() {
        String custom = resolveCustomLogoUrl();
        return custom != null ? custom : DEFAULT_LOGO_URL;
    }

    @Transactional(readOnly = true)
    public String getAppNom() {
        try {
            Parametre nom = parametreRepository.findByCodeParametre("app.nom");
            if (nom != null && nom.getValeurParametre() != null && !nom.getValeurParametre().isBlank()) {
                return nom.getValeurParametre();
            }
        } catch (Exception ignored) {
        }
        return DEFAULT_APP_NOM;
    }

    private String resolveCustomLogoUrl() {
        try {
            ParametreRepository.LogoMeta meta = parametreRepository.findLogoMeta("app.logo");
            if (meta == null || meta.getContentType() == null || meta.getContentType().isBlank()) {
                return null;
            }
            String version = meta.getValeurParametre();
            if (version == null || version.isBlank()) {
                return "/parametres/logo";
            }
            return "/parametres/logo?v=" + version;
        } catch (Exception e) {
            // Mauvaise BDD / table absente / connexion KO → logo de base
            return null;
        }
    }

    @Transactional(readOnly = true)
    public LogoData getLogoData() {
        Parametre logoParam = parametreRepository.findByCodeParametre("app.logo");
        if (logoParam == null
                || logoParam.getContenuBinaire() == null
                || logoParam.getContenuBinaire().length == 0) {
            return null;
        }
        String contentType = logoParam.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        String fileName = extraireNomFichier(logoParam.getValeurParametre());
        if (fileName == null) {
            fileName = "logo";
        }
        return new LogoData(logoParam.getContenuBinaire(), contentType, fileName);
    }

    public void updateLogo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        try {
            String extension = extraireExtension(file);
            String contentType = normaliserContentType(file.getContentType(), extension);
            String fileName = "logo_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            Parametre logoParam = parametreRepository.findByCodeParametre("app.logo");
            if (logoParam == null) {
                logoParam = new Parametre();
                logoParam.setCodeParametre("app.logo");
                logoParam.setLibelleParametre("Logo de l'application");
            }
            logoParam.setValeurParametre(fileName);
            logoParam.setContentType(contentType);
            logoParam.setContenuBinaire(file.getBytes());
            parametreRepository.save(logoParam);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du logo", e);
        }
    }

    public void deleteLogo() {
        Parametre logoParam = parametreRepository.findByCodeParametre("app.logo");
        if (logoParam == null) {
            return;
        }
        logoParam.setValeurParametre(null);
        logoParam.setContentType(null);
        logoParam.setContenuBinaire(null);
        parametreRepository.save(logoParam);
    }

    private String normaliserContentType(String contentType, String extension) {
        if (contentType != null && CONTENT_TYPES_AUTORISES.contains(contentType.toLowerCase(Locale.ROOT))) {
            return contentType;
        }
        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            case ".ico" -> "image/x-icon";
            case ".svg" -> "image/svg+xml";
            default -> "image/png";
        };
    }

    private String extraireExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        }
        if (!EXTENSIONS_AUTORISEES.contains(extension)) {
            String contentType = file.getContentType();
            if (contentType != null) {
                if (contentType.contains("png")) {
                    extension = ".png";
                } else if (contentType.contains("jpeg") || contentType.contains("jpg")) {
                    extension = ".jpg";
                } else if (contentType.contains("gif")) {
                    extension = ".gif";
                } else if (contentType.contains("webp")) {
                    extension = ".webp";
                } else if (contentType.contains("svg")) {
                    extension = ".svg";
                } else if (contentType.contains("icon")) {
                    extension = ".ico";
                } else {
                    extension = ".png";
                }
            } else {
                extension = ".png";
            }
        }
        return extension;
    }

    private String extraireNomFichier(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        String trimmed = valeur.trim().replace('\\', '/');
        int slash = trimmed.lastIndexOf('/');
        String fileName = slash >= 0 ? trimmed.substring(slash + 1) : trimmed;
        if (fileName.isBlank() || fileName.contains("..")) {
            return null;
        }
        return fileName;
    }

    public record LogoData(byte[] bytes, String contentType, String fileName) {}
}
