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


@Controller
@RequestMapping("/artistes")
public class ArtisteController {

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
                       @RequestParam(required = false) String supprimerPhoto) {

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
                e.printStackTrace();
                return "artiste/edit";
            }
        }


        artisteService.save(artiste);

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
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .body(resource);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        artisteService.deleteById(id);
        return "redirect:/artistes/liste";
    }
}