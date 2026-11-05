package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.model.Utilisateur;
import com.hellmetz.festival.security.UtilisateurDetails;
import com.hellmetz.festival.service.ParametreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Rend les variables communes disponibles automatiquement
 * dans tous les templates Thymeleaf :
 * - utilisateurConnecte : entité métier Utilisateur (aucune requête BDD,
 *   l'utilisateur vit déjà en session via Spring Security)
 * - theme, appNom, appLogo, hasCustomLogo : paramètres de l'application
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private static final String DEFAULT_APP_NOM = "SIOE";

    @Autowired
    private ParametreService parametreService;

    @ModelAttribute("utilisateurConnecte")
    public Utilisateur utilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        if (auth.getPrincipal() instanceof UtilisateurDetails ud) {
            return ud.getUtilisateur();
        }
        return null;
    }

    @ModelAttribute
    public void ajouterParametresGlobaux(Model model) {
        // Thème
        String theme = "Sombre";
        try {
            Parametre themeParam = parametreService.findByCode("affichage.theme");
            if (themeParam != null && themeParam.getValeurParametre() != null) {
                theme = themeParam.getValeurParametre();
            }
        } catch (Exception ignored) {
            // BDD KO → valeur par défaut
        }
        model.addAttribute("theme", theme);

        // Nom de l'application
        String appNom = DEFAULT_APP_NOM;
        try {
            Parametre nomParam = parametreService.findByCode("app.nom");
            if (nomParam != null
                    && nomParam.getValeurParametre() != null
                    && !nomParam.getValeurParametre().isBlank()) {
                appNom = nomParam.getValeurParametre();
            }
        } catch (Exception ignored) {
            // BDD KO → valeur par défaut
        }
        model.addAttribute("appNom", appNom);

        // Logo
        try {
            model.addAttribute("appLogo", parametreService.getLogoUrl());
            model.addAttribute("hasCustomLogo", parametreService.hasCustomLogo());
        } catch (Exception e) {
            model.addAttribute("appLogo", ParametreService.DEFAULT_LOGO_URL);
            model.addAttribute("hasCustomLogo", false);
        }
    }
}