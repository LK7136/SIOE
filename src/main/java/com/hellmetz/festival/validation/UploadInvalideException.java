package com.hellmetz.festival.validation;

/**
 * Levée quand un fichier envoyé ne respecte pas les règles (taille ou format).
 * Le message est écrit pour être affiché tel quel à l'utilisateur.
 */
public class UploadInvalideException extends RuntimeException {

    public UploadInvalideException(String message) {
        super(message);
    }
}