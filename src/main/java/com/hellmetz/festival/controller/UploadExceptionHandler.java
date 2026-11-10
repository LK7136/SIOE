package com.hellmetz.festival.controller;

import com.hellmetz.festival.validation.UploadInvalideException;
import com.hellmetz.festival.validation.UploadValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Transforme les erreurs d'upload (taille / format) en message affiché à l'utilisateur.
 * On renvoie vers la page d'où il vient et le message est lu par layout.html
 * (attribut "flash" : il n'existe que pour la page suivante).
 */
@ControllerAdvice
public class UploadExceptionHandler {

    private static final String PAGE_PAR_DEFAUT = "/dashboard";

    private final UploadValidator uploadValidator;

    public UploadExceptionHandler(UploadValidator uploadValidator) {
        this.uploadValidator = uploadValidator;
    }

    // Format refusé (levée par UploadValidator dans les contrôleurs)
    @ExceptionHandler(UploadInvalideException.class)
    public String fichierRefuse(UploadInvalideException ex,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        return retourAuFormulaire(ex.getMessage(), request, redirectAttributes);
    }

    // Fichier trop volumineux (levée par Spring avant même d'entrer dans le contrôleur)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fichierTropVolumineux(HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        return retourAuFormulaire(uploadValidator.messageTailleDepassee(), request, redirectAttributes);
    }

    private String retourAuFormulaire(String message,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("erreurUpload", message);
        return "redirect:" + pageDeRetour(request);
    }

    /**
     * Reprend le chemin (et les paramètres, ex. ?id=5) de la page précédente.
     * On ne garde que le chemin : la redirection reste toujours sur notre application.
     */
    private String pageDeRetour(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URI uri = new URI(referer);
                String chemin = uri.getRawPath();
                String contexte = request.getContextPath();
                if (chemin != null && chemin.startsWith(contexte)) {
                    chemin = chemin.substring(contexte.length());
                    // "//" serait interprété comme un autre site : on refuse
                    if (chemin.startsWith("/") && !chemin.startsWith("//")) {
                        return uri.getRawQuery() == null ? chemin : chemin + "?" + uri.getRawQuery();
                    }
                }
            } catch (URISyntaxException e) {
                // Referer illisible : on utilise la page par défaut
            }
        }
        return PAGE_PAR_DEFAUT;
    }
}