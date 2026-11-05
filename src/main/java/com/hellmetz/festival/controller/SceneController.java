package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Scene;
import com.hellmetz.festival.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

//pour recuper les fichier uploader d'une scene et les afficher quand on clique sur voir
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/scenes")
public class SceneController {
    @Autowired
    private SceneService sceneService;

    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/Desktop/StageHellMetz/uploads/";

    @GetMapping("/liste")
    public String liste(Model model) {
        model.addAttribute("scenes", sceneService.findAll());
        model.addAttribute("pageTitle", "HellMetz - Scenes");
        model.addAttribute("activeMenu", "templates");
        return "scene/list";
    }


    @GetMapping("/ajouter")
    public String edit(@RequestParam(required = false) Long id, Model model)  {

        if (id != null) {
            Scene concert = sceneService.findById(id);
            model.addAttribute("scene", concert);
            model.addAttribute("pageTitle", "Modifier la scene - HellMetz");
        } else {
            model.addAttribute("scene", new Scene());
            model.addAttribute("pageTitle", "Nouvel scene - HellMetz");
        }

        model.addAttribute("scenes", sceneService.findAll());
        return "scene/edit";
    }


    @PostMapping("/edit")
    public String save(@ModelAttribute Scene scene,
                       @RequestParam("planTechnique") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                // 1. Créer le dossier s'il n'existe pas
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 2. Nettoyer et sécuriser le nom du fichier
                String fileName = file.getOriginalFilename();
                Path targetLocation = uploadPath.resolve(fileName);

                // 3. Copier le fichier physiquement dans le dossier
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                // 4. Enregistrer le NOM du fichier dans l'objet scène
                scene.setUrlPlanTechnique(fileName);

            } catch (IOException e) {
                e.printStackTrace();
                // Optionnel : ajouter une erreur personnalisée si l'écriture échoue
                return "/scenes/edit";
            }
        }

        // 5. Sauvegarder la scène en BDD via le service
        sceneService.save(scene);

        return "redirect:/scenes/liste";
    }


    //pour recuper les fichier uploader d'une scene et les afficher quand on clique sur voir
    @GetMapping("/uploads/{nomFichier:.+}")
    public ResponseEntity<Resource> voirFichier(@PathVariable String nomFichier) throws IOException {
        Path cheminFichier = Paths.get(UPLOAD_DIR).resolve(nomFichier).normalize();
        Resource resource = new UrlResource(cheminFichier.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomFichier + "\"")
                .body(resource);
    }



    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        sceneService.deleteById(id);
        return "redirect:/scenes/liste";
    }

}
