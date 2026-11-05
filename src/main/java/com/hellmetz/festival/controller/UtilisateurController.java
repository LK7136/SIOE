package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Permission;
import com.hellmetz.festival.model.Role;
import com.hellmetz.festival.model.Utilisateur;
import com.hellmetz.festival.service.PermissionService;
import com.hellmetz.festival.service.RoleService;
import com.hellmetz.festival.security.UtilisateurDetails;
import com.hellmetz.festival.service.UtilisateurDetailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Utilisateur nouvelUtilisateur = new Utilisateur();
        model.addAttribute("utilisateur", nouvelUtilisateur);

        alimenterPermissions(model, nouvelUtilisateur);

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
                       @RequestParam(required = false) String motDePasse,
                       RedirectAttributes ra) {
        Role role = roleService.findById(idRole);
        if (role == null) {
            ra.addFlashAttribute("erreur", "Rôle introuvable.");
            return "redirect:/festival/utilisateurs/liste";
        }
        if (utilisateur.getId() != null) {
            Utilisateur existing = utilisateurDetailService.findById(utilisateur.getId());
            if (existing == null) {
                return "redirect:/festival/utilisateurs/liste";
            }
            if (utilisateurDetailService.isAdmin(existing)
                    && !"ADMIN".equals(role.getCodeRole())
                    && utilisateurDetailService.countAdmins() <= 1) {
                ra.addFlashAttribute("erreur", "Impossible de retirer le rôle du dernier administrateur.");
                return "redirect:/festival/utilisateurs/liste";
            }
            existing.setNom(utilisateur.getNom());
            existing.setPrenom(utilisateur.getPrenom());
            existing.setEmail(utilisateur.getEmail());
            existing.setIdentifiant(utilisateur.getIdentifiant());
            existing.setActif(actif != null && actif);
            if (motDePasse != null && !motDePasse.isBlank()) {
                utilisateurDetailService.changerMotDePasse(existing, motDePasse);
            }
            if (existing.getRoles() == null) {
                existing.setRoles(new java.util.ArrayList<>());
            } else {
                existing.getRoles().clear();
            }
            existing.getRoles().add(role);
            utilisateurDetailService.save(existing);
        } else {
            utilisateur.setRoles(new java.util.ArrayList<>(java.util.List.of(role)));
            utilisateurDetailService.creerUtilisateur(utilisateur);
        }
        return "redirect:/festival/utilisateurs/liste";
    }

    @PostMapping("/festival/utilisateurs/delete/{id}")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Utilisateur cible = utilisateurDetailService.findById(id);
        if (cible == null) {
            return "redirect:/festival/utilisateurs/liste";
        }
        if (auth != null && auth.getPrincipal() instanceof UtilisateurDetails ud
                && ud.getUtilisateur().getId().equals(id)) {
            ra.addFlashAttribute("erreur", "Vous ne pouvez pas supprimer votre propre compte.");
            return "redirect:/festival/utilisateurs/liste";
        }
        if (utilisateurDetailService.isAdmin(cible) && utilisateurDetailService.countAdmins() <= 1) {
            ra.addFlashAttribute("erreur", "Impossible de supprimer le dernier administrateur.");
            return "redirect:/festival/utilisateurs/liste";
        }
        utilisateurDetailService.deleteById(id);
        return "redirect:/festival/utilisateurs/liste";
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

        List<Long> utilisateurPermissionsIds = new java.util.ArrayList<>();

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

