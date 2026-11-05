package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.service.ParametreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/parametres")
public class ParametreController {

    @Autowired
    private ParametreService parametreService;

    @GetMapping("/liste")
    public String edit(Model model) {
        model.addAttribute("parametres", parametreService.findAll());
        model.addAttribute("pageTitle", "HellMetz - Paramètres");
        model.addAttribute("activeMenu", "parametres");

        return "parametre/list";
    }

    @PostMapping
    public String save(@RequestParam List<Long> ids,
                       @RequestParam List<String> valeurs) {
        for (int i = 0; i < ids.size(); i++) {
            Parametre p = parametreService.findById(ids.get(i));
            p.setValeurParametre(valeurs.get(i));
            parametreService.save(p);
        }

        return "redirect:/parametres/liste";
    }
}