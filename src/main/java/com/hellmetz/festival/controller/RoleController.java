package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Role;
import com.hellmetz.festival.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Principal;

@RestController
public class RoleController {
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    @Autowired
    private RoleService roleService;

    @PostMapping("/roles")
    public ResponseEntity<?> creerRole(@RequestBody Map<String, String> body, Principal principal) {

        String libelle = body.get("libelle");

        if (libelle == null || libelle.isBlank()) {
            log.warn("Création de rôle refusée : libellé absent (utilisateur {})", principal.getName());
            return ResponseEntity.badRequest().body("Le libellé du rôle est obligatoire.");
        }

        Role role = new Role();
        role.setLibelle(libelle.trim());
        Role saved = roleService.save(role);
        log.info("Rôle {} ('{}') créé par l'utilisateur {}", saved.getIdRole(), saved.getLibelle(), principal.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("idRole",  saved.getIdRole());
        response.put("libelle", saved.getLibelle());

        return ResponseEntity.ok(response);
    }
}