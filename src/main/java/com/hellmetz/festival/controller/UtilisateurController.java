package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Permission;
import com.hellmetz.festival.model.Role;
import com.hellmetz.festival.model.Utilisateur;
import com.hellmetz.festival.service.PermissionService;
import com.hellmetz.festival.service.RoleService;
import com.hellmetz.festival.service.UtilisateurDetailService;
import com.hellmetz.festival.validation.Creation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public String register(@Validated(Creation.class) @ModelAttribute Utilisateur utilisateur,
                           BindingResult result,   // doit suivre immédiatement l'objet validé
                           @RequestParam(required = false) Long idRole,
                           Model model) {

        // 1. Unicité : on ne vérifie que si le champ est déjà valide (évite d'empiler les messages)
        if (!result.hasFieldErrors("identifiant")
                && utilisateurDetailService.identifiantExiste(utilisateur.getIdentifiant())) {
            result.rejectValue("identifiant", "identifiant.existant", "Cet identifiant est déjà utilisé");
        }
        if (!result.hasFieldErrors("email")
                && utilisateurDetailService.emailExiste(utilisateur.getEmail())) {
            result.rejectValue("email", "email.existant", "Cette adresse e-mail est déjà utilisée");
        }

        // 2. Rôle : paramètre séparé de l'entité, donc contrôlé à la main
        Role roleChoisi = (idRole != null) ? roleService.findById(idRole) : null;
        if (roleChoisi == null) {
            model.addAttribute("erreurRole", "Veuillez choisir un rôle");
        }

        if (result.hasErrors() || roleChoisi == null) {
            return reafficherInscription(utilisateur, idRole, model);
        }

        utilisateur.setRoles(List.of(roleChoisi));

        try {
            utilisateurDetailService.creerUtilisateur(utilisateur);
            return "redirect:/login?success=created";
        } catch (DataIntegrityViolationException e) {
            // Filet de sécurité (ex. deux créations simultanées) : le détail SQL reste dans les logs
            log.warn("Création de l'utilisateur '{}' refusée par la base : {}",
                    utilisateur.getIdentifiant(), e.getMostSpecificCause().getMessage());
            model.addAttribute("erreur",
                    "Impossible de créer le compte : cet identifiant ou cette adresse e-mail est déjà utilisé.");
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création de l'utilisateur '{}'",
                    utilisateur.getIdentifiant(), e);
            model.addAttribute("erreur",
                    "Une erreur est survenue lors de la création du compte. Veuillez réessayer.");
        }
        return reafficherInscription(utilisateur, idRole, model);
    }

    /** Réaffiche le formulaire d'inscription avec tout ce dont la page a besoin. */
    private String reafficherInscription(Utilisateur utilisateur, Long idRole, Model model) {
        alimenterPermissions(model, utilisateur);          // roles, permissions, permissionsParRole
        model.addAttribute("idRoleChoisi", idRole);        // pour garder le rôle sélectionné
        return "register";
    }


    @GetMapping("/festival/utilisateurs/liste")
    public String liste(Model model) {
        model.addAttribute("utilisateurs", utilisateurDetailService.findAll());
        return "utilisateur/list";
    }


    @GetMapping("/festival/utilisateurs/ajouter")
    public String edit(@RequestParam(required = false) Long id, Model model) {
        Utilisateur utilisateurExistant;

        if (id != null) {
            utilisateurExistant = utilisateurDetailService.findById(id);
            model.addAttribute("pageTitle", "Modifier l'utilisateur - HellMetz");
        } else {
            utilisateurExistant = new Utilisateur();
            model.addAttribute("pageTitle", "Nouvel utilisateur - HellMetz");
        }
        model.addAttribute("utilisateur", utilisateurExistant);

        alimenterPermissions(model, utilisateurExistant);

        return "utilisateur/edit";
    }

    // INCHANGÉ pour l'instant (étape 2)
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