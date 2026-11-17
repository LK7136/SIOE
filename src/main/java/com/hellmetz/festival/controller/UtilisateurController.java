package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Permission;
import com.hellmetz.festival.model.Role;
import com.hellmetz.festival.model.Utilisateur;
import com.hellmetz.festival.service.PermissionService;
import com.hellmetz.festival.service.RoleService;
import com.hellmetz.festival.service.UtilisateurDetailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UtilisateurController {

    @Autowired
    private UtilisateurDetailService utilisateurDetailService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String success,
                            HttpServletRequest request,
                            Model model) {

        if ("created".equals(success)) {
            model.addAttribute("erreur", "Le compte a été créé avec succès !");
        }

        Object exceptionEnSession = request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

        if (exceptionEnSession != null) {
            if (exceptionEnSession instanceof BadCredentialsException) {
                model.addAttribute("typeErreur", "SaisieIncorrecte");
            } else if (exceptionEnSession instanceof InternalAuthenticationServiceException) {
                model.addAttribute("typeErreur", "ServIndisponible");
            } else {
                model.addAttribute("typeErreur", "Erreur");
            }
        }

        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        Utilisateur nouvelUtilisateur = new Utilisateur();
        model.addAttribute("utilisateur", nouvelUtilisateur);
        alimenterPermissions(model, nouvelUtilisateur);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Utilisateur utilisateur,
                           @RequestParam Long idRole,
                           @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                           Model model) {

        Role roleChoisi = roleService.findById(idRole);

        if (roleChoisi == null) {
            model.addAttribute("erreur", "Rôle invalide.");
            alimenterPermissions(model, utilisateur);
            return "register";
        }

        utilisateur.setRoles(new ArrayList<>(List.of(roleChoisi)));
        utilisateurDetailService.enregistrerPhoto(utilisateur, photoFile);

        try {
            utilisateurDetailService.creerUtilisateur(utilisateur);
            return "redirect:/login?success=created";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur : " + e.getMessage());
            alimenterPermissions(model, utilisateur);
            return "register";
        }
    }

    @GetMapping("/festival/utilisateurs/liste")
    public String liste(Model model) {
        model.addAttribute("utilisateurs", utilisateurDetailService.findAll());
        model.addAttribute("activeMenu", "utilisateurs");
        return "utilisateur/list";
    }

    @GetMapping("/festival/utilisateurs/ajouter")
    public String edit(@RequestParam(required = false) Long id, Model model) {
        Utilisateur utilisateurExistant;

        if (id != null) {
            utilisateurExistant = utilisateurDetailService.findById(id);
            model.addAttribute("pageTitle", "Modifier l'utilisateur");
        } else {
            utilisateurExistant = new Utilisateur();
            model.addAttribute("pageTitle", "Nouvel utilisateur");
        }
        model.addAttribute("utilisateur", utilisateurExistant);
        alimenterPermissions(model, utilisateurExistant);
        return "utilisateur/edit";
    }

    @PostMapping("/festival/utilisateurs/edit")
    public String save(@ModelAttribute Utilisateur utilisateur,
                       @RequestParam Long idRole,
                       @RequestParam(required = false) Boolean actif,
                       @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                       @RequestParam(value = "supprimerPhoto", required = false) String supprimerPhoto) {
        Role role = roleService.findById(idRole);
        if (utilisateur.getId() != null) {
            Utilisateur existing = utilisateurDetailService.findById(utilisateur.getId());
            if (existing == null) {
                return "redirect:/festival/utilisateurs/liste";
            }
            existing.setNom(utilisateur.getNom());
            existing.setPrenom(utilisateur.getPrenom());
            existing.setEmail(utilisateur.getEmail());
            existing.setIdentifiant(utilisateur.getIdentifiant());
            if (existing.getRoles() == null) {
                existing.setRoles(new ArrayList<>());
            } else {
                existing.getRoles().clear();
            }
            if (role != null) {
                existing.getRoles().add(role);
            }
            existing.setActif(actif != null && actif);
            if ("true".equals(supprimerPhoto)) {
                utilisateurDetailService.supprimerPhoto(existing);
            } else {
                utilisateurDetailService.enregistrerPhoto(existing, photoFile);
            }
            utilisateurDetailService.save(existing);
        } else {
            if (role != null) {
                utilisateur.setRoles(new ArrayList<>(List.of(role)));
            }
            utilisateurDetailService.enregistrerPhoto(utilisateur, photoFile);
            utilisateurDetailService.creerUtilisateur(utilisateur);
        }
        return "redirect:/festival/utilisateurs/liste";
    }

    @GetMapping("/festival/utilisateurs/delete/{id}")
    public String delete(@PathVariable Long id) {
        utilisateurDetailService.deleteById(id);
        return "redirect:/festival/utilisateurs/liste";
    }

    @GetMapping("/festival/utilisateurs/photo/{id}")
    public ResponseEntity<Resource> voirPhoto(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurDetailService.findById(id);
        if (utilisateur == null || !utilisateur.hasPhoto()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = utilisateur.getPhotoContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        String fileName = utilisateur.getUrlPhoto() != null ? utilisateur.getUrlPhoto() : "photo";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentLength(utilisateur.getContenuPhoto().length)
                .body(new ByteArrayResource(utilisateur.getContenuPhoto()));
    }

    private void alimenterPermissions(Model model, Utilisateur utilisateur){
        List<Role> roles = roleService.findAllAvecPermissions();
        Map<Long, List<Long>> permissionsParRole = new HashMap<>();

        for (Role role : roles) {
            List<Long> ids = role.getPermissions().stream()
                    .map(Permission::getIdPermission)
                    .collect(Collectors.toList());
            permissionsParRole.put(role.getIdRole(), ids);
        }

        model.addAttribute("permissions", permissionService.findAll());
        model.addAttribute("roles", roles);
        model.addAttribute("permissionsParRole", permissionsParRole);

        List<Long> utilisateurPermissionsIds = new ArrayList<>();

        if (utilisateur != null && utilisateur.getRoles() != null){
            for (Role roleUtilisateur : utilisateur.getRoles()) {
                List<Long> ids = permissionsParRole.get(roleUtilisateur.getIdRole());
                if (ids != null) {
                    for (Long id : ids) {
                        if (!utilisateurPermissionsIds.contains(id)) {
                            utilisateurPermissionsIds.add(id);
                        }
                    }
                }
            }
        }
        model.addAttribute("utilisateurPermissionsIds", utilisateurPermissionsIds);
    }
}
