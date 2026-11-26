package com.hellmetz.festival.service.impl;

import com.hellmetz.festival.config.StorageProperties;
import com.hellmetz.festival.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements StorageService {

    private final Path rootLocation;

    // liste des types d'image pouvant etre uploader
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "image/svg+xml"
    );

    public FileStorageServiceImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // validation du fichier
        validateImageFile(file);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Chemin invalide détecté : " + originalFilename);
        }

        // génération d'un nom unique (UUID + extension d'origine)
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + (extension != null ? "." + extension.toLowerCase() : "");

        // création du dossier cible si nécessaire
        Path targetDir = this.rootLocation.resolve(subDirectory).normalize();

        try {
            Files.createDirectories(targetDir);
            Path destinationFile = targetDir.resolve(uniqueFilename).normalize();

            // ecriture physique
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Échec de l'enregistrement du fichier : " + originalFilename, e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        // vérification du type envoyé par le navigateur
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Format de fichier non supporté. Seules les images (JPG, PNG, GIF, WEBP, SVG) sont autorisées.");
        }

        // inspection du contenu binaire
        if (!"image/svg+xml".equalsIgnoreCase(contentType) && !"image/webp".equalsIgnoreCase(contentType)) {
            try (InputStream input = file.getInputStream()) {
                if (ImageIO.read(input) == null) {
                    throw new IllegalArgumentException("Le fichier transmis est corrompu ou n'est pas une image valide.");
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Impossible de lire le contenu du fichier.", e);
            }
        }
    }

    @Override
    public void delete(String filename, String subDirectory) {
        if (filename == null || filename.isBlank()) return;

        try {
            Path fileToDelete = this.rootLocation.resolve(subDirectory).resolve(filename).normalize();
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            // message d'erreur
            System.err.println("Impossible de supprimer le fichier physique : " + filename);
        }
    }
}