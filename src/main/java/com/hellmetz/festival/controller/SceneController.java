package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Scene;
import com.hellmetz.festival.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/scenes")
public class SceneController {

    @Autowired
    private SceneService sceneService;

    // Dossier relatif à la racine du projet (portable entre tous les postes de l'équipe)
    private static final String UPLOAD_DIR = "uploads/scenes_plans/";

    @GetMapping("/liste")
    public String liste(Model model) {
        model.addAttribute("scenes", sceneService.findAll());
        model.addAttribute("pageTitle", "Scenes");
        model.addAttribute("activeMenu", "scenes");
        return "scene/list";
    }

    @GetMapping("/ajouter")
    public String edit(@RequestParam(required = false) Long id, Model model) {
        Scene scene;
        if (id != null) {
            scene = sceneService.findById(id);
            model.addAttribute("pageTitle", "Modifier la scene");
        } else {
            scene = new Scene();
            model.addAttribute("pageTitle", "Nouvelle scene");
        }

        // Vérifie si le fichier référencé en base existe physiquement sur le poste courant
        boolean planExiste = false;
        if (scene != null && scene.getUrlPlanTechnique() != null && !scene.getUrlPlanTechnique().isBlank()) {
            Path chemin = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize()
                    .resolve(scene.getUrlPlanTechnique()).normalize();
            planExiste = Files.exists(chemin) && Files.isReadable(chemin);
        }

        model.addAttribute("scene", scene);
        model.addAttribute("planExiste", planExiste);
        model.addAttribute("scenes", sceneService.findAll());
        model.addAttribute("activeMenu", "scenes");
        return "scene/edit";
    }

    @PostMapping("/edit")
    public String save(@ModelAttribute Scene scene,
                       @RequestParam("planTechnique") MultipartFile file,
                       RedirectAttributes redirectAttributes) {

        String redirectOnError = (scene.getId() != null)
                ? "redirect:/scenes/ajouter?id=" + scene.getId()
                : "redirect:/scenes/ajouter";

        if (file != null && !file.isEmpty()) {
            // 1. Validation stricte du format PDF
            if (!isValidPdf(file)) {
                redirectAttributes.addFlashAttribute("error",
                        "Format invalide : seuls les fichiers PDF sont acceptés pour le plan technique.");
                return redirectOnError;
            }

            try {
                // 2. Créer le dossier relatif s'il n'existe pas
                Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 3. Nettoyer et sécuriser le nom du fichier
                String originalName = file.getOriginalFilename();
                String cleanFileName = StringUtils.cleanPath(originalName != null ? originalName : "plan.pdf");
                if (cleanFileName.contains("..")) {
                    redirectAttributes.addFlashAttribute("error", "Nom de fichier non valide.");
                    return redirectOnError;
                }

                Path targetLocation = uploadPath.resolve(cleanFileName).normalize();
                if (!targetLocation.startsWith(uploadPath)) {
                    redirectAttributes.addFlashAttribute("error", "Chemin de destination non autorisé.");
                    return redirectOnError;
                }

                // 4. Copier le fichier physiquement dans le dossier
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                // 5. Enregistrer le nom du fichier dans l'objet scène
                scene.setUrlPlanTechnique(cleanFileName);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error",
                        "Erreur lors de l'enregistrement du fichier PDF sur le serveur.");
                return redirectOnError;
            }
        }

        // 6. Sauvegarder la scène en BDD via le service
        sceneService.save(scene);

        return "redirect:/scenes/liste";
    }

    // Récupère les fichiers uploadés d'une scène et les affiche dans l'iframe
    @GetMapping("/uploads/{nomFichier:.+}")
    public ResponseEntity<Resource> voirFichier(@PathVariable String nomFichier) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        Path cheminFichier = uploadPath.resolve(nomFichier).normalize();

        // Protection contre le Path Traversal (../)
        if (!cheminFichier.startsWith(uploadPath)) {
            return ResponseEntity.badRequest().build();
        }

        Resource resource = new UrlResource(cheminFichier.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        ContentDisposition contentDisposition = ContentDisposition.inline()
                .filename(resource.getFilename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        sceneService.deleteById(id);
        return "redirect:/scenes/liste";
    }

    /**
     * Vérifie que le fichier est bien un PDF valide (extension, Content-Type et signature %PDF-)
     */
    private boolean isValidPdf(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();

        boolean hasPdfExtension = filename != null && filename.toLowerCase().endsWith(".pdf");
        boolean hasPdfMime = "application/pdf".equalsIgnoreCase(contentType);

        if (!hasPdfExtension || !hasPdfMime) {
            return false;
        }

        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[5];
            if (is.read(header) < 5) {
                return false;
            }
            String magic = new String(header, StandardCharsets.US_ASCII);
            return "%PDF-".equals(magic);
        } catch (IOException e) {
            return false;
        }
    }
}