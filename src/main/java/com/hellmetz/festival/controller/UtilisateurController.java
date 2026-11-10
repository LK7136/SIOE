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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
                           @RequestParam(required = false) List<Long> idsPermissions,
                           Model model) {

        Role roleChoisi = roleService.findByIdAvecPermissions(idRole);

        if (roleChoisi == null) {
            model.addAttribute("erreur", "Rôle invalide.");
            alimenterPermissions(model, utilisateur);
            return "register";
        }

        utilisateur.setRoles(java.util.List.of(roleChoisi));
        utilisateur.setPermissions(permissionsIndividuelles(roleChoisi, idsPermissions));

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
                       @RequestParam(required = false) List<Long> idsPermissions) {
        Role role = roleService.findByIdAvecPermissions(idRole);
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle invalide.");
        }
        Set<Permission> individuelles = permissionsIndividuelles(role, idsPermissions);

        if (utilisateur.getId() != null) {
            Utilisateur existing = utilisateurDetailService.findById(utilisateur.getId());
            existing.setRoles(java.util.List.of(role));
            existing.setPermissions(individuelles);
            existing.setActif(actif != null && actif);
            utilisateurDetailService.save(existing);
        } else {
            // Création
            utilisateur.setRoles(java.util.List.of(role));
            utilisateur.setPermissions(individuelles);
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

        // Permissions effectives (role + individuelles) : meme calcul que getAuthorities()
        List<Long> utilisateurPermissionsIds = utilisateur == null
                ? new java.util.ArrayList<>()
                : utilisateur.getPermissionsEffectives().stream()
                        .map(Permission::getIdPermission)
                        .collect(Collectors.toList());
        model.addAttribute("utilisateurPermissionsIds", utilisateurPermissionsIds);
    }

    /**
     * Permissions cochees dans le formulaire, moins celles deja portees par le role :
     * seules les permissions supplementaires sont stockees en base, ce qui permet
     * a une evolution future du role de se repercuter sur l'utilisateur.
     * Les ids inconnus sont ignores (findAllById).
     */
    private Set<Permission> permissionsIndividuelles(Role role, List<Long> idsPermissions) {
        if (idsPermissions == null || idsPermissions.isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> idsDuRole = role.getPermissions() == null ? Set.of()
                : role.getPermissions().stream()
                        .map(Permission::getIdPermission)
                        .collect(Collectors.toSet());
        return permissionService.findAllById(idsPermissions).stream()
                .filter(p -> !idsDuRole.contains(p.getIdPermission()))
                .collect(Collectors.toCollection(HashSet::new));
    }

}
