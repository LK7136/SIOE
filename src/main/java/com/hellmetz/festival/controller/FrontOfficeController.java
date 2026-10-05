package com.hellmetz.festival.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;

/**
 * Controleur du site public (front office).
 * Chaque methode renvoie une page de templates/front/.
 */
@Controller
public class FrontOfficeController {

    // vrai si un utilisateur est connecte sert au lien du menu
    @ModelAttribute("connecte")
    public boolean connecte(Principal principal) {
        return principal != null;
    }

    @GetMapping("/")
    public String accueil() {
        return "front/accueil";
    }

    @GetMapping("/programme")
    public String programme() {
        return "front/programme";
    }

    @GetMapping("/line-up")
    public String lineUp() {
        return "front/line-up";
    }

    @GetMapping("/infos-pratiques")
    public String infosPratiques() {
        return "front/infos-pratiques";
    }

    @GetMapping("/contact")
    public String contact() {
        return "front/contact";
    }
}