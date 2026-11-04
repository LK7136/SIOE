package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.service.ParametreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public String save(@RequestParam(required = false) List<Long> ids,
                       @RequestParam(required = false) List<String> valeurs,
                       @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                       @RequestParam(value = "deleteLogo", required = false) String deleteLogo) {

        if (ids != null && valeurs != null) {
            int taille = Math.min(ids.size(), valeurs.size());
            for (int i = 0; i < taille; i++) {
                Parametre p = parametreService.findById(ids.get(i));
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

    @GetMapping("/uploads/{nomFichier:.+}")
    public ResponseEntity<Resource> voirLogo(@PathVariable String nomFichier) throws IOException {
        Path cheminFichier = parametreService.findLogoFile(nomFichier);
        if (cheminFichier == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(cheminFichier.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(cheminFichier);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + cheminFichier.getFileName() + "\"")
                .body(resource);
    }
}


