package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Parametre;
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

import java.util.List;

@Controller
@RequestMapping("/parametres")

public class ParametreController {

    @Autowired
    private ParametreService parametreService;

    @GetMapping("/liste")
    public String edit(Model model) {
        model.addAttribute("parametres", parametreService.findAll());
        model.addAttribute("pageTitle", "Paramètres");
        model.addAttribute("activeMenu", "parametres");

        return "parametre/list";
    }

    @PostMapping
    public String save(@RequestParam(required = false) List<Long> ids,
                       @RequestParam(required = false) List<String> valeurs,
                       @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                       @RequestParam(value = "deleteLogo", required = false) String deleteLogo) {

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
}
