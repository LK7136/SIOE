package com.hellmetz.festival.controller;

import com.hellmetz.festival.model.Role;
import com.hellmetz.festival.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;

/**
 * ENDPOINT INTERNE AU BACK-OFFICE (ce n'est pas une API publique).
 * <p>
 * Justification : appelé en AJAX par la modale « Créer un nouveau rôle »
 * du formulaire register.html. Il n'a pas de sens en dehors de cet écran,
 * donc il reste dans le back-office sous /interne/** au lieu de migrer vers l'API.
 * <p>
 * Sécurité : réservé au rôle ADMIN (voir SecurityConfig) et protégé par le jeton CSRF.
 * <p>
 * Format d'erreur : toute erreur renvoie un JSON {"erreur": "message"}
 * avec le code HTTP correspondant (400, 409 ou 500).
 */
@RestController
@RequestMapping("/interne/roles")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    private static final int LIBELLE_MAX = 50;

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> creerRole(@RequestBody Map<String, String> body,
                                                         Principal principal) {

        String brut = body == null ? null : body.get("libelle");

        if (brut == null || brut.isBlank()) {
            log.warn("Création de rôle refusée : libellé absent (utilisateur {})", principal.getName());
            return erreur(HttpStatus.BAD_REQUEST, "Le libellé du rôle est obligatoire.");
        }

        String libelle = brut.trim();

        if (libelle.length() > LIBELLE_MAX) {
            log.warn("Création de rôle refusée : libellé trop long ({} caractères)", libelle.length());
            return erreur(HttpStatus.BAD_REQUEST,
                    "Le libellé du rôle ne doit pas dépasser " + LIBELLE_MAX + " caractères.");
        }

        // Le code du rôle (colonne obligatoire en base) est déduit du libellé : « Bénévole » -> BENEVOLE
        String code = genererCode(libelle);

        if (code.isEmpty()) {
            log.warn("Création de rôle refusée : le libellé ne contient ni lettre ni chiffre");
            return erreur(HttpStatus.BAD_REQUEST,
                    "Le libellé du rôle doit contenir au moins une lettre ou un chiffre.");
        }

        boolean existeDeja = roleService.findAll().stream()
                .anyMatch(r -> libelle.equalsIgnoreCase(r.getLibelle()) || code.equalsIgnoreCase(r.getCodeRole()));
        if (existeDeja) {
            log.warn("Création de rôle refusée : le rôle '{}' (code {}) existe déjà", libelle, code);
            return erreur(HttpStatus.CONFLICT, "Ce rôle existe déjà.");
        }

        try {
            Role role = new Role();
            role.setLibelle(libelle);
            role.setCodeRole(code);
            Role saved = roleService.save(role);
            log.info("Rôle {} ('{}', code {}) créé par l'utilisateur {}",
                    saved.getIdRole(), saved.getLibelle(), saved.getCodeRole(), principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("idRole", saved.getIdRole(), "libelle", saved.getLibelle()));
        } catch (RuntimeException e) {
            log.error("Échec de la création du rôle '{}'", libelle, e);
            return erreur(HttpStatus.INTERNAL_SERVER_ERROR, "Le rôle n'a pas pu être créé.");
        }
    }

    /** « Bénévole » -> « BENEVOLE », « Chef de scène » -> « CHEF_DE_SCENE ». */
    private static String genererCode(String libelle) {
        String sansAccents = Normalizer.normalize(libelle, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sansAccents.toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }

    private static ResponseEntity<Map<String, Object>> erreur(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(Map.of("erreur", message));
    }
}