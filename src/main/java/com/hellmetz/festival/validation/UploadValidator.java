package com.hellmetz.festival.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;

/**
 * Composant unique qui regroupe les règles de validation des fichiers envoyés
 * (taille + format). Utilisé par ArtisteController, GroupeController et SceneController.
 */
@Component
public class UploadValidator {

    private static final Set<String> EXTENSIONS_IMAGE = Set.of("jpg", "jpeg", "png");
    private static final Set<String> TYPES_MIME_IMAGE = Set.of("image/jpeg", "image/png");

    private static final Set<String> EXTENSIONS_PDF = Set.of("pdf");
    private static final Set<String> TYPES_MIME_PDF = Set.of("application/pdf");

    private final DataSize tailleMax;

    // On relit la limite de application.properties : une seule valeur à modifier
    public UploadValidator(@Value("${spring.servlet.multipart.max-file-size}") DataSize tailleMax) {
        this.tailleMax = tailleMax;
    }

    /** Images de groupes / artistes : .jpg, .jpeg, .png uniquement. */
    public void validerImage(MultipartFile fichier) {
        valider(fichier, EXTENSIONS_IMAGE, TYPES_MIME_IMAGE,
                "Format refusé : seuls les fichiers .jpg, .jpeg et .png sont acceptés.");
    }

    /** Plan technique d'une scène : .pdf uniquement. */
    public void validerPdf(MultipartFile fichier) {
        valider(fichier, EXTENSIONS_PDF, TYPES_MIME_PDF,
                "Format refusé : seul le format .pdf est accepté.");
    }

    /** Message affiché quand la taille maximale est dépassée (aussi utilisé par UploadExceptionHandler). */
    public String messageTailleDepassee() {
        return "Fichier trop volumineux : la taille maximale autorisée est de "
                + tailleMax.toMegabytes() + " Mo.";
    }

    private void valider(MultipartFile fichier, Set<String> extensions, Set<String> typesMime, String messageFormat) {
        // Aucun fichier choisi dans le formulaire : rien à valider (l'upload est facultatif)
        if (fichier == null || fichier.isEmpty()) {
            return;
        }

        // Filet de sécurité : Spring refuse déjà les fichiers trop gros avant d'arriver ici
        if (fichier.getSize() > tailleMax.toBytes()) {
            throw new UploadInvalideException(messageTailleDepassee());
        }

        // Vérification de l'extension du nom de fichier
        String extension = StringUtils.getFilenameExtension(fichier.getOriginalFilename());
        if (extension == null || !extensions.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new UploadInvalideException(messageFormat);
        }

        // Vérification du type MIME annoncé par le navigateur
        String typeMime = fichier.getContentType();
        if (typeMime == null || !typesMime.contains(typeMime.toLowerCase(Locale.ROOT))) {
            throw new UploadInvalideException(messageFormat);
        }
    }
}