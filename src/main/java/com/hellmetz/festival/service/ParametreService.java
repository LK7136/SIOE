package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.repository.ParametreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ParametreService {

    private static final Path LOGO_UPLOAD_DIR = Paths.get(
            System.getProperty("user.home"), "Desktop", "StageHellMetz", "uploads", "logo");
    private static final Path LEGACY_DESKTOP_UPLOAD_DIR = Paths.get(
            System.getProperty("user.home"), "Desktop", "StageHellMetz", "uploads");
    private static final Path LEGACY_PROJECT_UPLOAD_DIR = Paths.get("uploads");
    private static final Set<String> EXTENSIONS_AUTORISEES = Set.of(".png", ".jpg", ".jpeg", ".gif", ".webp", ".ico", ".svg");

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
    public String getLogoUrl() {
        Parametre logoParam = parametreRepository.findByCodeParametre("app.logo");
        if (logoParam == null) {
            return null;
        }
        String fileName = extraireNomFichier(logoParam.getValeurParametre());
        if (fileName == null) {
            return null;
        }
        return "/parametres/uploads/" + fileName;
    }

    public Path findLogoFile(String nomFichier) {
        String fileName = extraireNomFichier(nomFichier);
        if (fileName == null) {
            return null;
        }
        for (Path dir : List.of(LOGO_UPLOAD_DIR, LEGACY_DESKTOP_UPLOAD_DIR, LEGACY_PROJECT_UPLOAD_DIR)) {
            Path dirAbs = dir.toAbsolutePath().normalize();
            Path file = dirAbs.resolve(fileName).normalize();
            if (file.startsWith(dirAbs) && Files.isRegularFile(file)) {
                return file;
            }
        }
        return null;
    }

    public void updateLogo(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                Files.createDirectories(LOGO_UPLOAD_DIR);

                String extension = extraireExtension(file);
                String fileName = "logo_" + UUID.randomUUID().toString().substring(0, 8) + extension;
                Path filePath = LOGO_UPLOAD_DIR.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                Parametre logoParam = parametreRepository.findByCodeParametre("app.logo");
                if (logoParam == null) {
                    logoParam = new Parametre();
                    logoParam.setCodeParametre("app.logo");
                    logoParam.setLibelleParametre("Logo de l'application");
                }
                logoParam.setValeurParametre(fileName);
                parametreRepository.save(logoParam);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'enregistrement du logo", e);
            }
        }
    }

    public void deleteLogo() {
        Parametre logoParam = parametreRepository.findByCodeParametre("app.logo");
        if (logoParam == null) {
            return;
        }
        Path file = findLogoFile(logoParam.getValeurParametre());
        if (file != null) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la suppression du logo", e);
            }
        }
        logoParam.setValeurParametre(null);
        parametreRepository.save(logoParam);
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
}


