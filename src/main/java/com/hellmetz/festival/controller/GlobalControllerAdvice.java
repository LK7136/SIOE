package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Utilisateur;
import com.hellmetz.festival.parametre.CodesParametres;
import com.hellmetz.festival.security.UtilisateurDetails;
import com.hellmetz.festival.service.ParametreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private static final String DEFAULT_APP_NOM = "SIOE";
    private static final String DEFAULT_THEME = "Sombre";

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
        model.addAttribute("theme", parametreService.getString(CodesParametres.AFFICHAGE_THEME, DEFAULT_THEME));
        model.addAttribute("appNom", parametreService.getString(CodesParametres.APP_NOM, DEFAULT_APP_NOM));
        model.addAttribute("appLogo", parametreService.getLogoUrl());
        model.addAttribute("hasCustomLogo", parametreService.hasCustomLogo());
    }
}
