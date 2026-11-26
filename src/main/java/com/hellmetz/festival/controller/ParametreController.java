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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/parametres")
public class ParametreController {

    private static final String CODE_LOGO = "app.logo";

    // Codes des paramètres qui deviennent personnels (pour ne plus les afficher dans les réglages communs)
    private static final Set<String> CODES_PREFERENCES_USER = Set.of(
            "app.elements_par_page", "elements_par_page", "elementsParPage"
    );

    @Autowired
    private ParametreService parametreService;

    @Autowired
    private EditionService editionService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("/liste")
    public String edit(Model model, Principal principal) {
        model.addAttribute("parametres", parametreService.findAll());
        model.addAttribute("utilisateur", getUtilisateurConnecte(principal).orElse(null));
        model.addAttribute("editions", editionService.findAll());
        model.addAttribute("pageTitle", "Paramètres");
        model.addAttribute("activeMenu", "parametres");

        return "parametre/list";
    }

    @PostMapping("/preferences")
    public String savePreferences(@RequestParam Integer elementsParPage, Principal principal) {
        enregistrerElementsParPage(principal, elementsParPage);
        return "redirect:/parametres/liste";
    }

    @PostMapping
    public String save(@RequestParam(required = false) List<Long> ids,
                       @RequestParam(required = false) List<String> valeurs,
                       @RequestParam(required = false) Integer elementsParPage,
                       @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                       @RequestParam(value = "deleteLogo", required = false) String deleteLogo,
                       Principal principal) {

        enregistrerParametresCommuns(ids, valeurs);
        Optional.ofNullable(elementsParPage)
                .ifPresent(nombre -> enregistrerElementsParPage(principal, nombre));
        traiterLogo(logoFile, deleteLogo);

        return "redirect:/parametres/liste";
    }

    @GetMapping({"/logo", "/uploads/{nomFichier:.+}"})
    public ResponseEntity<Resource> voirLogo(@PathVariable(required = false) String nomFichier) {
        return Optional.ofNullable(parametreService.getLogoData())
                .map(this::construireReponseLogo)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private void enregistrerParametresCommuns(List<Long> ids, List<String> valeurs) {
        List<Long> idsRecus = Objects.requireNonNullElse(ids, List.of());
        List<String> valeursRecues = Objects.requireNonNullElse(valeurs, List.of());
        int nombre = Math.min(idsRecus.size(), valeursRecues.size());

        for (int i = 0; i < nombre; i++) {
            enregistrerParametre(idsRecus.get(i), valeursRecues.get(i));
        }
    }

    private void enregistrerParametre(Long id, String valeur) {
        Parametre parametre = parametreService.findById(id);
        if (CODE_LOGO.equals(parametre.getCodeParametre())) {
            return;
        }
        parametre.setValeurParametre(valeur);
        parametreService.save(parametre);
    }

    private void enregistrerElementsParPage(Principal principal, Integer elementsParPage) {
        getUtilisateurConnecte(principal).ifPresent(utilisateur -> {
            utilisateur.setElementsParPage(elementsParPage);
            utilisateurRepository.save(utilisateur);
        });
    }

    private void traiterLogo(MultipartFile logoFile, String deleteLogo) {
        Optional.ofNullable(logoFile)
                .filter(fichier -> !fichier.isEmpty())
                .ifPresentOrElse(
                        parametreService::updateLogo,
                        () -> supprimerLogoSiDemande(deleteLogo));
    }

    private void supprimerLogoSiDemande(String deleteLogo) {
        if ("true".equals(deleteLogo)) {
            parametreService.deleteLogo();
        }
    }

    private ResponseEntity<Resource> construireReponseLogo(ParametreService.LogoData logo) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, logo.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + logo.fileName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .contentLength(logo.bytes().length)
                .body(new ByteArrayResource(logo.bytes()));
    }

    private Optional<Utilisateur> getUtilisateurConnecte(Principal principal) {
        return Optional.ofNullable(principal)
                .map(Principal::getName)
                .flatMap(utilisateurRepository::findByIdentifiant);
    }
}