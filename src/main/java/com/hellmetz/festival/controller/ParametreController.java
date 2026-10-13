package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.service.ParametreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Principal;


@Controller
@RequestMapping("/parametres")
public class ParametreController {
    private static final Logger log = LoggerFactory.getLogger(ParametreController.class);
    @Autowired
    private ParametreService parametreService;

    @GetMapping("/liste")
    public String edit(Model model) {
        log.debug("Liste des paramètres");
        model.addAttribute("parametres", parametreService.findAll());
        model.addAttribute("pageTitle", "HellMetz - Paramètres");
        model.addAttribute("activeMenu", "parametres");

        return "parametre/list";
    }

    @PostMapping
    public String save(@RequestParam List<Long> ids,
                       @RequestParam List<String> valeurs,
                       Principal principal) {
        if (ids.size() != valeurs.size()) {
            log.warn("Paramètres : {} identifiants reçus pour {} valeurs", ids.size(), valeurs.size());
        }
        for (int i = 0; i < ids.size(); i++) {
            Parametre p = parametreService.findById(ids.get(i));
            p.setValeurParametre(valeurs.get(i));
            parametreService.save(p);
            log.info("Paramètre {} modifié par l'utilisateur {}", ids.get(i), principal.getName());
        }

        return "redirect:/parametres/liste";
    }
}