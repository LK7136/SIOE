package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Role;
import com.hellmetz.festival.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/roles")
    public ResponseEntity<?> creerRole(@RequestBody Map<String, String> body) {

        String libelle = body.get("libelle");

        if (libelle == null || libelle.isBlank()) {
            return ResponseEntity.badRequest().body("Le libellé du rôle est obligatoire.");
        }

        Role role = new Role();
        role.setLibelle(libelle.trim());
        Role saved = roleService.save(role);


        Map<String, Object> response = new HashMap<>();
        response.put("idRole",  saved.getIdRole());
        response.put("libelle", saved.getLibelle());

        return ResponseEntity.ok(response);
    }
}