package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Artiste;
import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.service.ArtisteService;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
@RequestMapping("/artistes")
public class ArtisteController {

    private static final Logger log = LoggerFactory.getLogger(ArtisteController.class);

    @Autowired
    private ArtisteService artisteService;
    @Autowired
    private StyleService styleService;

    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/Desktop/StageHellMetz/uploads/artistes/";

    @GetMapping("/liste")
    public String liste(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") String taille,
            Model model) {

        log.debug("Liste des artistes : page={}, taille={}", page, taille);

        boolean tout = "tout".equalsIgnoreCase(taille);
        Page<Artiste> pageArtistes = tout
                ? artisteService.findTout()
                : artisteService.findPage(page, parseTaille(taille));

        model.addAttribute("artistes", pageArtistes.getContent());
        model.addAttribute("page", pageArtistes);
        model.addAttribute("taille", taille);
        model.addAttribute("pageTitle", "HellMetz - Artistes");
        model.addAttribute("activeMenu", "templates");
        return "artiste/list";
    }

    private int parseTaille(String taille) {
        try {
            int t = Integer.parseInt(taille);
            return t > 0 ? t : 10;
        } catch (NumberFormatException e) {
            log.warn("Taille de page invalide '{}', valeur par défaut 10 utilisée", taille);
            return 10;
        }
    }

    @GetMapping("/ajouter")
    public String edit(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) Integer idGroupeParam,
                       Model model) {
        if (id != null) {
            Artiste artiste = artisteService.findById(id);
            model.addAttribute("artiste", artiste);
            model.addAttribute("pageTitle", "Modifier l'artiste - HellMetz");
        } else {
            model.addAttribute("artiste", new Artiste());
            model.addAttribute("pageTitle", "Nouvel artiste - HellMetz");
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
                       @RequestParam(required = false) MultipartFile urlPhotoArtiste,
                       @RequestParam(required = false) String supprimerPhoto,
                       RedirectAttributes redirectAttributes,
                       Principal principal) {

        if ("true".equals(supprimerPhoto)) {
            artiste.setUrlPhoto(null);
        }

        if (urlPhotoArtiste != null && !urlPhotoArtiste.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = urlPhotoArtiste.getOriginalFilename();
                Path targetLocation = uploadPath.resolve(fileName);
                Files.copy(urlPhotoArtiste.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                artiste.setUrlPhoto(fileName);

            } catch (IOException e) {
                log.error("Échec de l'upload du fichier {} pour l'artiste {}",
                        urlPhotoArtiste.getOriginalFilename(), artiste.getId(), e);
                redirectAttributes.addFlashAttribute("erreur", "La photo n'a pas pu être envoyée.");
                return "redirect:/artistes/ajouter" + (artiste.getId() != null ? "?id=" + artiste.getId() : "");
            }
        }

        boolean creation = artiste.getId() == null;
        artisteService.save(artiste);

        log.info("Artiste {} {} par l'utilisateur {}",
                artiste.getId(), creation ? "créé" : "modifié", principal.getName());

        if (idGroupeParam != null) {
            return "redirect:/groupe/edit?id=" + idGroupeParam;
        }
        return "redirect:/artistes/liste";
    }

    @GetMapping("/uploads/{nomFichier:.+}")
    public ResponseEntity<Resource> voirPhoto(@PathVariable String nomFichier) throws IOException {
        Path cheminFichier = Paths.get(UPLOAD_DIR).resolve(nomFichier).normalize();
        Resource resource = new UrlResource(cheminFichier.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            log.warn("Photo introuvable ou illisible : {}", nomFichier);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .body(resource);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Principal principal) {
        artisteService.deleteById(id);
        log.info("Artiste {} supprimé par l'utilisateur {}", id, principal.getName());
        return "redirect:/artistes/liste";
    }
}