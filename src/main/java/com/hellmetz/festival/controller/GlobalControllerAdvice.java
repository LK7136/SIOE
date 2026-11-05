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

import com.hellmetz.festival.model.Parametre;

/**
 * Rend la variable {@code utilisateurConnecte} (entite metier Utilisateur)
 * disponible automatiquement dans tous les templates Thymeleaf.
 * Aucune requete BDD : l'utilisateur vit deja en session via Spring Security.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

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
    public void ajouterTheme(Model model) {
        String theme = "Sombre";
        try {
            Parametre themeParam = parametreService.findByCode("affichage.theme");
            if (themeParam != null && themeParam.getValeurParametre() != null) {
                theme = themeParam.getValeurParametre();
            }
        } catch (Exception ignored) {
            // BDD KO → valeurs par défaut
        }
        model.addAttribute("theme", theme);

        try {
            model.addAttribute("appLogo", parametreService.getLogoUrl());
            model.addAttribute("hasCustomLogo", parametreService.hasCustomLogo());
        } catch (Exception e) {
            model.addAttribute("appLogo", ParametreService.DEFAULT_LOGO_URL);
            model.addAttribute("hasCustomLogo", false);
        }
    }
}
