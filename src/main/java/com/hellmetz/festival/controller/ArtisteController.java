package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Artiste;
import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.service.ArtisteService;
import com.hellmetz.festival.service.StorageService;
import com.hellmetz.festival.service.StyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
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


@Controller
@RequestMapping("/artistes")
public class ArtisteController {

    private static final String SUB_DIR = "artiste_image";

    @Autowired
    private ArtisteService artisteService;
    @Autowired
    private StyleService styleService;

    private final StorageService storageService;

    public ArtisteController(ArtisteService artisteService,
                             StyleService styleService,
                             StorageService storageService) {
        this.artisteService = artisteService;
        this.styleService = styleService;
        this.storageService = storageService;
    }


    @GetMapping("/liste")
    public String liste(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") String taille,
            Model model) {

        boolean tout = "tout".equalsIgnoreCase(taille);
        Page<Artiste> pageArtistes = tout
                ? artisteService.findTout()
                : artisteService.findPage(page, parseTaille(taille));

        model.addAttribute("artistes", pageArtistes.getContent());
        model.addAttribute("page", pageArtistes);
        model.addAttribute("taille", taille);
        model.addAttribute("pageTitle", "Artistes");
        model.addAttribute("activeMenu", "artiste");
        return "artiste/list";
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
                       @RequestParam(required = false) Integer idGroupeParam,
                       Model model) {
        if (id != null) {
            Artiste artiste = artisteService.findById(id);
            if (artiste == null) {
                return "redirect:/artistes/liste";
            }
            model.addAttribute("artiste", artiste);
            model.addAttribute("pageTitle", "Modifier l'artiste");
        } else {
            model.addAttribute("artiste", new Artiste());
            model.addAttribute("pageTitle", "Nouvel artiste");
        }

        if (idGroupeParam != null) {
            model.addAttribute("idGroupeParam", idGroupeParam);
        }

        model.addAttribute("styles", styleService.findAll());
        return "artiste/edit";
    }

    @PostMapping("/edit")
    public String save(@ModelAttribute Artiste artiste,
                       @RequestParam(required = false) Integer idGroupeParam,
                       @RequestParam(value = "artistePhoto", required = false) MultipartFile file,
                       @RequestParam(value = "supprimerPhoto", defaultValue = "false") boolean supprimerPhoto,
                       RedirectAttributes redirectAttributes) {

        // garder la photo existante en cas de modification
        if (artiste.getId() != null) {
            Artiste existant = artisteService.findById(artiste.getId());
            if (existant != null) {
                artiste.setUrlPhoto(existant.getUrlPhoto());
            }
        }

        try {
            // suppression de la photo
            if (supprimerPhoto && artiste.getUrlPhoto() != null) {
                storageService.delete(artiste.getUrlPhoto(), SUB_DIR);
                artiste.setUrlPhoto(null);
            }

            // upload de la photo
            if (file != null && !file.isEmpty()) {
                // Nettoyage de l'ancien fichier
                if (artiste.getUrlPhoto() != null) {
                    storageService.delete(artiste.getUrlPhoto(), SUB_DIR);
                }
                // Enregistrement du nouveau fichier
                String newFilename = storageService.store(file, SUB_DIR);
                artiste.setUrlPhoto(newFilename);
            }

            artisteService.save(artiste);

            if (idGroupeParam != null) {
                return "redirect:/groupes/ajouter?id=" + idGroupeParam;
            }
            return "redirect:/artistes/liste";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            String redirectUrl = "/artistes/ajouter";
            if (artiste.getId() != null) {
                redirectUrl += "?id=" + artiste.getId();
                if (idGroupeParam != null) {
                    redirectUrl += "&idGroupeParam=" + idGroupeParam;
                }
            } else if (idGroupeParam != null) {
                redirectUrl += "?idGroupeParam=" + idGroupeParam;
            }
            return "redirect:" + redirectUrl;
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        Artiste artiste = artisteService.findById(id);
        if (artiste == null) {
            flash.addFlashAttribute("erreur", "Artiste introuvable.");
            return "redirect:/artistes/liste";
        }

        if (artiste.getUrlPhoto() != null) {
            storageService.delete(artiste.getUrlPhoto(), SUB_DIR);
        }

        artisteService.deleteById(id);
        flash.addFlashAttribute("message", "Artiste supprimé.");
        return "redirect:/artistes/liste";
    }
}