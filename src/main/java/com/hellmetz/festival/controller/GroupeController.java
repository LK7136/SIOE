package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Concert;
import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.model.Style;
import com.hellmetz.festival.repository.StyleRepository;
import com.hellmetz.festival.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/groupes")
public class GroupeController {

    @Autowired
    private GroupeService groupeService;
    @Autowired
    private ArtisteService artisteService;
    @Autowired
    private ConcertService concertService;
    @Autowired
    private StyleService styleService;
    @Autowired
    private StyleRepository styleRepository;

    private final StorageService storageService;


    private static final String SUB_DIR = "groupe_image";

    public GroupeController(GroupeService groupeService, StorageService storageService) {
        this.groupeService = groupeService;
        this.storageService = storageService;
    }

    @GetMapping("/liste")
    public String listGroupes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") String taille,
            Model model) {

        boolean tout = "tout".equalsIgnoreCase(taille);
        Page<Groupe> pageGroupes = tout
                ? groupeService.findTout()
                : groupeService.findPage(page, parseTaille(taille));

        model.addAttribute("groupes", pageGroupes.getContent());
        model.addAttribute("page", pageGroupes);
        model.addAttribute("taille", taille);
        model.addAttribute("pageTitle", "Groupes");
        model.addAttribute("activeMenu", "groupes");
        return "groupe/list";
    }

    private int parseTaille(String taille) {
        try {
            int t = Integer.parseInt(taille);
            return t > 0 ? t : 10;
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    @GetMapping("/ajouter")
    public String edit(@RequestParam(required = false) Long id,
                       @RequestParam(defaultValue = "0") int concertPage,
                       @RequestParam(defaultValue = "10") String concertTaille,
                       Model model) {

        boolean tout = "tout".equalsIgnoreCase(concertTaille);

        List<Style> tousLesStyles = styleService.findAll();
        model.addAttribute("tousLesStyles", tousLesStyles);

        if (id != null) {
            Groupe groupe = groupeService.findByIdWithStyles(id);
            model.addAttribute("groupe", groupe);
            model.addAttribute("pageTitle", "Modifier le groupe");
            model.addAttribute("artistes", artisteService.findByGroupeId(id));

            Page<Concert> pageConcerts = tout
                    ? concertService.findDisponiblesOuAnnulesOuGroupeTout(id)
                    : concertService.findDisponiblesOuAnnulesOuGroupe(id, concertPage, parseTaille(concertTaille));

            model.addAttribute("concertsDispo", pageConcerts.getContent());
            model.addAttribute("pageConcerts", pageConcerts);
            model.addAttribute("concertTaille", concertTaille);

        } else {
            model.addAttribute("groupe", new Groupe());
            model.addAttribute("pageTitle", "Nouveau groupe");
            model.addAttribute("artistes", java.util.Collections.emptyList());

            Page<Concert> pageConcerts = tout
                    ? concertService.findDisponiblesOuAnnulesTout()
                    : concertService.findDisponiblesOuAnnules(concertPage, parseTaille(concertTaille));

            model.addAttribute("concertsDispo", pageConcerts.getContent());
            model.addAttribute("pageConcerts", pageConcerts);
            model.addAttribute("concertTaille", concertTaille);
        }
        return "groupe/edit";
    }

    @PostMapping("/{groupeId}/concerts")
    public String saveConcerts(@PathVariable Long groupeId,
                               @RequestParam(required = false) List<Long> concertIds,
                               @RequestParam(required = false) List<Long> visibleConcertIds) {

        // si aucune case n'est cochée on initialise une liste vide
        // pour éviter les NullPointerException
        if (concertIds == null) {
            concertIds = java.util.Collections.emptyList();
        }

        //on execute que s'il y a des concerts affichés
        if (visibleConcertIds != null) {

            Groupe groupe = groupeService.findById(groupeId);

            for (int i = 0; i < visibleConcertIds.size(); i++) {

                Long idConcert = visibleConcertIds.get(i);
                Concert concert = concertService.findById(idConcert);

                //si on coche un/plusieur concert pour le grp = programé
                if (concertIds.contains(idConcert)) {
                    concert.setGroupe(groupe);
                    concert.setStatut("Programmé");
                }

                //si on décochée un/plusieur concert appartenant au grp = annulé
                else {
                    // On vérifie si ce concert appartenait bien à ce groupe avant de lui retirer
                    if (concert.getGroupe() != null && concert.getGroupe().getId().equals(groupeId)) {
                        concert.setGroupe(null);
                        concert.setStatut("Annulé");
                    }
                }

                concertService.save(concert);
            }
        }

        return "redirect:/groupes/ajouter?id=" + groupeId + "&tab=concerts";
    }


    @PostMapping("/edit")
    public String save(@ModelAttribute Groupe groupe,
                       @RequestParam(value = "groupePhoto", required = false) MultipartFile file,
                       @RequestParam(value = "supprimerPhoto", defaultValue = "false") boolean supprimerPhoto,
                       @RequestParam(value = "stylesId", required = false) List<Long> stylesId,
                       RedirectAttributes redirectAttributes) {


        if (groupe.getId() != null) {
            Groupe existant = groupeService.findById(groupe.getId());
            if (existant != null) {
                groupe.setUrlLogo(existant.getUrlLogo());
            }
        }

        try {

            // suppression de la photo
            if (supprimerPhoto && groupe.getUrlLogo() != null) {
                storageService.delete(groupe.getUrlLogo(), SUB_DIR);
                groupe.setUrlLogo(null);
            }

            // upload et remplacement de la photo
            if (file != null && !file.isEmpty()) {
                // nettoy de l'ancien fichier
                if (groupe.getUrlLogo() != null) {
                    storageService.delete(groupe.getUrlLogo(), SUB_DIR);
                }
                // Enregistrement du nouveau fichier dans "groupe_image"
                String newFilename = storageService.store(file, SUB_DIR);
                groupe.setUrlLogo(newFilename);
            }

            // Gestion des styles
            if (groupe.getStylesDuGroupe() != null) {
                groupe.getStylesDuGroupe().clear();
            } else {
                groupe.setStylesDuGroupe(new HashSet<>());
            }

            if (stylesId != null) {
                for (Long idStyle : stylesId) {
                    Style style = styleRepository.findById(idStyle)
                            .orElseThrow(() -> new IllegalArgumentException("Style introuvable : " + idStyle));
                    groupe.addStyles(style);
                }
            }

            groupeService.save(groupe);
            return "redirect:/groupes/liste";

        } catch (IllegalArgumentException e) {
            // Récupère les erreurs de validation (mauvais format, style introuvable, etc.)
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/groupes/ajouter" + (groupe.getId() != null ? "?id=" + groupe.getId() : "");
        }
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        Groupe groupe = groupeService.findById(id);
        if (groupe == null) {
            flash.addFlashAttribute("erreur", "Groupe introuvable.");
            return "redirect:/groupes/liste";
        }

        if (groupe.getUrlLogo() != null) {
            storageService.delete(groupe.getUrlLogo(), SUB_DIR);
        }

        groupeService.deleteById(id);
        flash.addFlashAttribute("message", "Groupe supprimé.");
        return "redirect:/groupes/liste";
    }
}