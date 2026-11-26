package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.model.Utilisateur;
import com.hellmetz.festival.repository.UtilisateurRepository;
import com.hellmetz.festival.service.EditionService;
import com.hellmetz.festival.service.ParametreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/parametres")
public class ParametreController {

    @Autowired
    private ParametreService parametreService;

    @Autowired
    private EditionService editionService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Codes des paramètres qui deviennent personnels (pour ne plus les afficher dans les réglages communs)
    private static final Set<String> CODES_PREFERENCES_USER = Set.of(
            "app.theme", "app.elements_par_page", "theme", "elements_par_page", "elementsParPage"
    );

    @GetMapping("/liste")
    public String edit(Model model, Principal principal) {
        Utilisateur utilisateur = (principal != null)
                ? utilisateurRepository.findByIdentifiant(principal.getName()).orElse(null)
                : null;

        model.addAttribute("parametres", parametreService.findAll());
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("editions", editionService.findAll());
        model.addAttribute("pageTitle", "Paramètres");
        model.addAttribute("activeMenu", "parametres");

        return "parametre/list";
    }

    @PostMapping("/preferences")
    public String savePreferences(@RequestParam String theme,
                                  @RequestParam Integer elementsParPage,
                                  Principal principal) {
        Utilisateur utilisateur = getUtilisateurConnecte(principal);
        if (utilisateur != null) {
            utilisateur.setElementsParPage(elementsParPage);
            utilisateurRepository.save(utilisateur);
        }
        return "redirect:/parametres/liste";
    }

    @PostMapping
    public String save(@RequestParam(required = false) List<Long> ids,
                       @RequestParam(required = false) List<String> valeurs,
                       @RequestParam(required = false) Integer elementsParPage,
                       @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                       @RequestParam(value = "deleteLogo", required = false) String deleteLogo,
                       Principal principal) {

        // 1. Sauvegarde des réglages communs (le paramètre éléments par page n'est plus dans ids/valeurs)
        if (ids != null && valeurs != null) {
            int taille = Math.min(ids.size(), valeurs.size());
            for (int i = 0; i < taille; i++) {
                Parametre p = parametreService.findById(ids.get(i));
                if ("app.logo".equals(p.getCodeParametre())) {
                    continue;
                }
                p.setValeurParametre(valeurs.get(i));
                parametreService.save(p);
            }
        }

        // 2. Sauvegarde de elementsParPage uniquement pour l'utilisateur connecté
        if (elementsParPage != null && principal != null) {
            utilisateurRepository.findByIdentifiant(principal.getName()).ifPresent(utilisateur -> {
                utilisateur.setElementsParPage(elementsParPage);
                utilisateurRepository.save(utilisateur);
            });
        }

        if (logoFile != null && !logoFile.isEmpty()) {
            parametreService.updateLogo(logoFile);
        } else if ("true".equals(deleteLogo)) {
            parametreService.deleteLogo();
        }

        return "redirect:/parametres/liste";
    }

    @GetMapping({"/logo", "/uploads/{nomFichier:.+}"})
    public ResponseEntity<Resource> voirLogo(@PathVariable(required = false) String nomFichier) {
        ParametreService.LogoData logo = parametreService.getLogoData();
        if (logo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, logo.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + logo.fileName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .contentLength(logo.bytes().length)
                .body(new ByteArrayResource(logo.bytes()));
    }

    private Utilisateur getUtilisateurConnecte(Principal principal) {
        if (principal == null) return null;
        return utilisateurRepository.findByIdentifiant(principal.getName()).orElse(null);
    }
}