package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Edition;
import com.hellmetz.festival.service.EditionService;
import com.hellmetz.festival.service.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/editions")
public class EditionController {

    private static final String SUB_DIR = "edition_affiche";

    private final EditionService editionService;
    private final StorageService storageService;

    public EditionController(EditionService editionService, StorageService storageService) {
        this.editionService = editionService;
        this.storageService = storageService;
    }


    @GetMapping("/liste")
    public String liste(Model model) {
        List<Edition> editions = editionService.findAll();
        model.addAttribute("editions", editions);
        model.addAttribute("pageTitle", "HellMetz - Editions");
        model.addAttribute("activeMenu", "templates");
        return "edition/list";
    }

    @GetMapping("/ajouter")
    public String edit(@RequestParam(required = false) Long id,
                       Model model) {
        if (id != null) {
            Edition edition = editionService.findById(id);
            if (edition == null) {
                return "redirect:/parametres/liste?tab=editions";
            }
            model.addAttribute("edition", edition);
            model.addAttribute("pageTitle", "Modifier l'édition - HellMetz");
        } else {
            model.addAttribute("edition", new Edition());
            model.addAttribute("pageTitle", "Nouvelle édition - HellMetz");
        }
        model.addAttribute("activeMenu", "parametres");
        return "edition/edit";
    }

    @PostMapping("/edit")
    public String save(@ModelAttribute Edition edition,
                       @RequestParam(value = "afficheFile", required = false) MultipartFile file,
                       @RequestParam(value = "supprimerAffiche", defaultValue = "false") boolean supprimerAffiche,
                       RedirectAttributes redirectAttributes) {



        if (edition.getId() != null) {
            Edition existante = editionService.findById(edition.getId());
            if (existante != null) {
                edition.setActif(existante.isActif());
                edition.setUrlAfficheOfficielle(existante.getUrlAfficheOfficielle());
            }
        } else {
            edition.setActif(false);
        }

        try {
            // suppression de l'affiche
            if (supprimerAffiche && edition.getUrlAfficheOfficielle() != null) {
                storageService.delete(edition.getUrlAfficheOfficielle(), SUB_DIR);
                edition.setUrlAfficheOfficielle(null);
            }

            // upload et remplacement de l'affiche
            if (file != null && !file.isEmpty()) {
                // nettoyage de l'ancien fichier physique si un nouveau est envoyé
                if (edition.getUrlAfficheOfficielle() != null) {
                    storageService.delete(edition.getUrlAfficheOfficielle(), SUB_DIR);
                }
                // enregistrement du nouveau fichier
                String newFilename = storageService.store(file, SUB_DIR);
                edition.setUrlAfficheOfficielle(newFilename);
            }
            editionService.save(edition);
            return "redirect:/parametres/liste?tab=editions";

        } catch (IllegalArgumentException e) {
            // recup les erreurs de validation (mauvais format)
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/editions/ajouter" + (edition.getId() != null ? "?id=" + edition.getId() : "");
        }
    }

    @PostMapping("/{id}/activer")
    public String activer(@PathVariable Long id) {
        editionService.activer(id);
        return "redirect:/parametres/liste?tab=editions";
    }

    @PostMapping("/{id}/supprimer")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        Edition edition = editionService.findById(id);

        if (edition.isActif()) {
            flash.addFlashAttribute("erreur", "Impossible de supprimer l'édition active.");
            return "redirect:/parametres/liste?tab=editions";
        }

        editionService.deleteById(id);
        flash.addFlashAttribute("message", "Édition supprimée");
        return "redirect:/parametres/liste?tab=editions";
    }
}
