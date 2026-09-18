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

        // chercher l'erreur d'authentification cachée en session par Spring Security
        Object exceptionEnSession = request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

        if (exceptionEnSession != null) {
            // erreur de saisie (Identifiant ou mot de passe incorrect)
            if (exceptionEnSession instanceof BadCredentialsException) {
                model.addAttribute("typeErreur", "SaisieIncorrecte");
            }
            // la VM ou serveur ne répond pas
            // (Spring lève souvent une InternalAuthenticationServiceException dans ce cas)
            else if (exceptionEnSession instanceof InternalAuthenticationServiceException) {
                model.addAttribute("typeErreur", "ServIndisponible");
            }
            // autre erreur au cas où
            else {
                model.addAttribute("typeErreur", "Erreur");
            }
        }

        return "login";
    }


    @GetMapping("/register")
    public String registerForm(Model model) {

        //ajouter
        List<Role> roles = roleService.findAllAvecPermissions();

        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("roles", roleService.findAll());

        //ajouter pour recup les perm
        model.addAttribute("permissions", permissionService.findAll());

        //ajouter pour recup les perm
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
            model.addAttribute("erreur", "Rôle invalide.");
            model.addAttribute("roles", roleService.findAll());
            return "register";
        }

        utilisateur.setRoles(java.util.List.of(roleChoisi));


        try {
            utilisateurDetailService.creerUtilisateur(utilisateur);
            return "redirect:/login?success=created";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur : " + e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            return "register";
        }
    }




    @GetMapping("/festival/utilisateurs/liste")
    public String liste(Model model) {
        model.addAttribute("utilisateurs", utilisateurDetailService.findAll());
        return "utilisateur/list";
    }


    @GetMapping("/festival/utilisateurs/ajouter")
    public String edit(@RequestParam(required = false) Long id, Model model) {
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
                       @RequestParam(required = false) Boolean actif) {
        Role role = roleService.findById(idRole);
        if (utilisateur.getId() != null) {
            Utilisateur existing = utilisateurDetailService.findById(utilisateur.getId());
            existing.setRoles(java.util.List.of(role));
            existing.setActif(actif != null && actif);
            utilisateurDetailService.save(existing);
        } else {
            // Création
            utilisateur.setRoles(java.util.List.of(role));
            utilisateurDetailService.creerUtilisateur(utilisateur);
        }
        return "redirect:/festival/utilisateurs/liste";
    }

    @GetMapping("/festival/utilisateurs/delete/{id}")
    public String delete(@PathVariable Long id) {
        utilisateurDetailService.deleteById(id);
        return "redirect:/festival/utilisateurs/liste";
    }


}

