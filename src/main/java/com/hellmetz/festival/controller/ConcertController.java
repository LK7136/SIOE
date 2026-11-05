package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Concert;
import com.hellmetz.festival.model.Edition;
import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.service.ConcertService;
import com.hellmetz.festival.service.ConcertService.FenetreFestival;
import com.hellmetz.festival.service.EditionService;
import com.hellmetz.festival.service.GroupeService;
import com.hellmetz.festival.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

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
        model.addAttribute("pageTitle", "Concerts");
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

        Concert concert;
        if (id != null) {
            concert = concertService.findById(id);
        } else {
            concert = new Concert();
            // Édition active présélectionnée, puis début pré-rempli à 18h00 le 1er jour du festival
            editionService.findAll().stream()
                    .filter(Edition::isActif)
                    .findFirst()
                    .ifPresent(concert::setEdition);

            concertService.getFenetre(concert.getEdition()).ifPresent(f ->
                    concert.setDateHeureDebut(
                            f.debut().toLocalDate().atTime(ConcertService.HEURE_DEBUT_PAR_DEFAUT)));
        }

        preparerFormulaire(concert, groupesPage, groupesTaille, new LinkedHashMap<>(), model);
        return "concert/edit";
    }

    @PostMapping("/edit")
    public String save(@ModelAttribute Concert concert,
                       BindingResult binding,
                       @RequestParam(value = "groupeId", required = false) Long groupeId,
                       Model model) {

        // 1. Validation (avant toute écriture)
        Map<String, String> erreurs = new LinkedHashMap<>();
        binding.getFieldErrors().forEach(e -> erreurs.putIfAbsent(e.getField(),
                e.getField().startsWith("dateHeure")
                        ? "Format de date invalide pour le champ « " + e.getField() + " »."
                        : "Valeur invalide pour le champ « " + e.getField() + " »."));
        concertService.validerDates(concert).forEach(erreurs::putIfAbsent);

        if (!erreurs.isEmpty()) {
            // On conserve le groupe coché pour ré-afficher le formulaire tel quel
            if (groupeId != null) {
                concert.setGroupe(groupeService.findById(groupeId));
            }
            preparerFormulaire(concert, 0, "10", erreurs, model);
            return "concert/edit";
        }

        // 2. Enregistrement (logique existante inchangée)
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

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        concertService.deleteById(id);
        return "redirect:/concerts/liste";
    }

    /** Alimente le modèle du formulaire (GET initial et ré-affichage après erreur). */
    private void preparerFormulaire(Concert concert, int groupesPage, String groupesTaille,
                                    Map<String, String> erreurs, Model model) {

        boolean tout = "tout".equalsIgnoreCase(groupesTaille);
        Page<Groupe> pageGroupes = tout
                ? groupeService.findGroupesSansConcertTout()
                : groupeService.findGroupesSansConcertPage(groupesPage, parseTaille(groupesTaille));

        model.addAttribute("groupesSansConcert", pageGroupes.getContent());
        model.addAttribute("pageGroupes", pageGroupes);
        model.addAttribute("groupesTaille", groupesTaille);

        model.addAttribute("concert", concert);
        model.addAttribute("pageTitle", concert.getId() != null
                ? "Modifier le concert - HellMetz"
                : "Nouveau concert - HellMetz");
        model.addAttribute("scenes", sceneService.findAll());
        model.addAttribute("editions", editionService.findAll());
        model.addAttribute("erreurs", erreurs);

        // Bornes des datetime-local : édition du concert, sinon paramètres
        Optional<FenetreFestival> fenetre = concertService.getFenetre(concert.getEdition());
        model.addAttribute("dateMin", fenetre.map(FenetreFestival::debutHtml).orElse(null));
        model.addAttribute("dateMax", fenetre.map(FenetreFestival::finHtml).orElse(null));

        // Bornes « paramètres » : utilisées par le JS quand aucune édition n'est sélectionnée
        Optional<FenetreFestival> parDefaut = concertService.getFenetre(null);
        model.addAttribute("defautMin", parDefaut.map(FenetreFestival::debutHtml).orElse(""));
        model.addAttribute("defautMax", parDefaut.map(FenetreFestival::finHtml).orElse(""));
    }
}