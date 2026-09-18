package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Concert;
import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.model.Style;
import com.hellmetz.festival.repository.StyleRepository;
import com.hellmetz.festival.service.ConcertService;
import com.hellmetz.festival.service.GroupeService;
import com.hellmetz.festival.service.ArtisteService;
import com.hellmetz.festival.service.StyleService;
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

    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/Desktop/StageHellMetz/uploads/groupes/";

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
        model.addAttribute("pageTitle", "HellMetz - Groupes");
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
            model.addAttribute("pageTitle", "Modifier le groupe - HellMetz");
            model.addAttribute("artistes", artisteService.findByGroupeId(id));

            Page<Concert> pageConcerts = tout
                    ? concertService.findDisponiblesOuAnnulesOuGroupeTout(id)
                    : concertService.findDisponiblesOuAnnulesOuGroupe(id, concertPage, parseTaille(concertTaille));

            model.addAttribute("concertsDispo", pageConcerts.getContent());
            model.addAttribute("pageConcerts", pageConcerts);
            model.addAttribute("concertTaille", concertTaille);

        } else {
            model.addAttribute("groupe", new Groupe());
            model.addAttribute("pageTitle", "Nouveau groupe - HellMetz");
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
                       @RequestParam(value = "stylesId", required = false) List<Long> stylesId,
                       @RequestParam(required = false) MultipartFile urlLogoGroupe,
                       @RequestParam(required = false) String supprimerPhoto) {

        if ("true".equals(supprimerPhoto)) {
            groupe.setUrlLogo(null);
        }

        if (urlLogoGroupe != null && !urlLogoGroupe.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = urlLogoGroupe.getOriginalFilename();
                Path targetLocation = uploadPath.resolve(fileName);
                Files.copy(urlLogoGroupe.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                groupe.setUrlLogo(fileName);

            } catch (IOException e) {
                e.printStackTrace();
                return "groupe/edit";
            }
        }

        // 1. On nettoie proprement les anciens styles associés en mémoire
        if (groupe.getStylesDuGroupe() != null) {
            groupe.getStylesDuGroupe().clear();
        } else {
            groupe.setStylesDuGroupe(new HashSet<>());
        }

        // 2. On va chercher et on ajoute les nouveaux styles cochés
        if (stylesId != null) {
            for (Long idStyle : stylesId) {
                Style style = styleRepository.findById(idStyle)
                        .orElseThrow(() -> new IllegalArgumentException("Style introuvable : " + idStyle));

                groupe.addStyles(style);
            }
        }

        groupeService.save(groupe);

        return "redirect:/groupes/liste";
    }

    @GetMapping("/uploads/{nomFichier:.+}")
    public ResponseEntity<Resource> voirPhoto(@PathVariable String nomFichier) throws IOException {
        Path cheminFichier = Paths.get(UPLOAD_DIR).resolve(nomFichier).normalize();
        Resource resource = new UrlResource(cheminFichier.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .body(resource);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        groupeService.deleteById(id);
        return "redirect:/groupes/liste";
    }
}