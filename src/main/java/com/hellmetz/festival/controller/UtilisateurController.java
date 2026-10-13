package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Permission;
import com.hellmetz.festival.model.Role;
import com.hellmetz.festival.model.Utilisateur;
import com.hellmetz.festival.service.PermissionService;
import com.hellmetz.festival.service.RoleService;
import com.hellmetz.festival.service.UtilisateurDetailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Principal;

@Controller
public class UtilisateurController {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurController.class);

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
                log.warn("Échec de connexion : identifiants incorrects");
                model.addAttribute("typeErreur", "SaisieIncorrecte");
            }

            else if (exceptionEnSession instanceof InternalAuthenticationServiceException) {
                log.error("Échec de connexion : serveur d'authentification indisponible");
                model.addAttribute("typeErreur", "ServIndisponible");
            }
            else {
                log.warn("Échec de connexion : erreur inattendue ({})", exceptionEnSession.getClass().getSimpleName());
                model.addAttribute("typeErreur", "Erreur");
            }
        }

        return "login";
    }


    @GetMapping("/register")
    public String registerForm(Model model) {
        log.debug("Affichage du formulaire d'inscription");

        List<Role> roles = roleService.findAllAvecPermissions();

        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("roles", roleService.findAll());

        model.addAttribute("permissions", permissionService.findAll());

        Map<Long, List<Long>> permissionsParRole = new HashMap<>();
        for (Role role : roles) {
            List<Long> ids = role.getPermissions().stream()
                    .map(Permission::getIdPermission)
                    .collect(Collectors.toList());
            permissionsParRole.put(role.getIdRole(), ids);
        }
        model.addAttribute("permissionsParRole", permissionsParRole);

        return "register";
    }





    @PostMapping("/register")
    public String register(@ModelAttribute Utilisateur utilisateur,
                           @RequestParam Long idRole,
                           Model model) {
        Role roleChoisi = roleService.findById(idRole);

        if (roleChoisi == null) {
            log.warn("Inscription refusée : rôle {} introuvable", idRole);
            model.addAttribute("erreur", "Rôle invalide.");
            model.addAttribute("roles", roleService.findAll());
            return "register";
        }

        utilisateur.setRoles(java.util.List.of(roleChoisi));

        try {
            utilisateurDetailService.creerUtilisateur(utilisateur);
            log.info("Compte créé (id {}) avec le rôle {}", utilisateur.getId(), idRole);
            return "redirect:/login?success=created";
        } catch (Exception e) {
            log.error("Échec de la création du compte avec le rôle {}", idRole, e);
            model.addAttribute("erreur", "Erreur : " + e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            return "register";
        }
    }




    @GetMapping("/festival/utilisateurs/liste")
    public String liste(Model model) {
        log.debug("Liste des utilisateurs");
        model.addAttribute("utilisateurs", utilisateurDetailService.findAll());
        return "utilisateur/list";
    }


    @GetMapping("/festival/utilisateurs/ajouter")
    public String edit(@RequestParam(required = false) Long id, Model model) {
        log.debug("Affichage du formulaire utilisateur, id={}", id);
        if (id != null) {
            model.addAttribute("utilisateur", utilisateurDetailService.findById(id));
            model.addAttribute("pageTitle", "Modifier l'utilisateur - HellMetz");
        } else {
            model.addAttribute("utilisateur", new Utilisateur());
            model.addAttribute("pageTitle", "Nouvel utilisateur - HellMetz");
        }
        model.addAttribute("roles", roleService.findAll());
        return "utilisateur/edit";
    }

    @PostMapping("/festival/utilisateurs/edit")
    public String save(@ModelAttribute Utilisateur utilisateur,
                       @RequestParam Long idRole,
                       @RequestParam(required = false) Boolean actif,
                       Principal principal) {
        Role role = roleService.findById(idRole);
        if (utilisateur.getId() != null) {
            Utilisateur existing = utilisateurDetailService.findById(utilisateur.getId());
            existing.setRoles(java.util.List.of(role));
            existing.setActif(actif != null && actif);
            utilisateurDetailService.save(existing);
            log.info("Utilisateur {} modifié par l'utilisateur {}", existing.getId(), principal.getName());
        } else {
            // Création
            utilisateur.setRoles(java.util.List.of(role));
            utilisateurDetailService.creerUtilisateur(utilisateur);
            log.info("Utilisateur {} créé par l'utilisateur {}", utilisateur.getId(), principal.getName());
        }
        return "redirect:/festival/utilisateurs/liste";
    }

    @GetMapping("/festival/utilisateurs/delete/{id}")
    public String delete(@PathVariable Long id, Principal principal) {
        utilisateurDetailService.deleteById(id);
        log.info("Utilisateur {} supprimé par l'utilisateur {}", id, principal.getName());
        return "redirect:/festival/utilisateurs/liste";
    }


}

