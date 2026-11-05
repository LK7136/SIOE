package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Concert;
import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.service.ConcertService;
import com.hellmetz.festival.service.EditionService;
import com.hellmetz.festival.service.GroupeService;
import com.hellmetz.festival.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/concerts")
public class ConcertController {

    @Autowired
    private ConcertService concertService;
    @Autowired
    private SceneService sceneService;
    @Autowired
    private EditionService editionService;
    @Autowired
    private GroupeService groupeService;

    @GetMapping("/liste")
    public String listConcerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") String taille,
            Model model) {

        boolean tout = "tout".equalsIgnoreCase(taille);
        Page<Concert> pageConcerts = tout
                ? concertService.findTout()
                : concertService.findPage(page, parseTaille(taille));

        model.addAttribute("concerts", pageConcerts.getContent());
        model.addAttribute("page", pageConcerts);
        model.addAttribute("taille", taille);
        model.addAttribute("pageTitle", "HellMetz - Concerts");
        model.addAttribute("activeMenu", "concerts");
        return "concert/list";
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
                       @RequestParam(defaultValue = "0") int groupesPage,
                       @RequestParam(defaultValue = "10") String groupesTaille,
                       Model model) {

        boolean tout = "tout".equalsIgnoreCase(groupesTaille);

        Page<Groupe> pageGroupes = tout
                ? groupeService.findGroupesSansConcertTout()
                : groupeService.findGroupesSansConcertPage(groupesPage, parseTaille(groupesTaille));

        model.addAttribute("groupesSansConcert", pageGroupes.getContent());
        model.addAttribute("pageGroupes", pageGroupes);
        model.addAttribute("groupesTaille", groupesTaille);

        if (id != null) {
            Concert concert = concertService.findById(id);
            model.addAttribute("concert", concert);
            model.addAttribute("pageTitle", "Modifier le concert - HellMetz");
        } else {
            model.addAttribute("concert", new Concert());
            model.addAttribute("pageTitle", "Nouveau concert - HellMetz");
        }

        model.addAttribute("scenes", sceneService.findAll());
        model.addAttribute("editions", editionService.findAll());

        return "concert/edit";
    }

    @PostMapping("/edit")
    public String save(@ModelAttribute Concert concert,
                       @RequestParam(value = "groupeId", required = false) Long groupeId) {

        // vérifie si le concert existait déjà et s'il avait un groupe
        boolean avaitUnGroupe = false;
        if (concert.getId() != null) {
            Concert existant = concertService.findById(concert.getId());
            avaitUnGroupe = existant.getGroupe() != null;
        }


        if (groupeId != null) {
            Groupe groupe = groupeService.findById(groupeId);
            concert.setGroupe(groupe);
            concert.setStatut("Programmé");
        } else {
            concert.setGroupe(null);

            if (avaitUnGroupe) {
                concert.setStatut("Annulé");
            } else {
                concert.setStatut("Non programmé");
            }
        }

        concertService.save(concert);
        return "redirect:/concerts/liste";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        concertService.deleteById(id);
        return "redirect:/concerts/liste";
    }
}