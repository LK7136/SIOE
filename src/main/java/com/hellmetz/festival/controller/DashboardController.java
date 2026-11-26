package com.hellmetz.festival.controller;

import com.hellmetz.festival.dashboard.*;
import com.hellmetz.festival.model.DashboardProperties;
import com.hellmetz.festival.service.DashboardPreferenceService;
import com.hellmetz.festival.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class DashboardController {


    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private static final ObjectMapper JSON = new ObjectMapper();

    private static final int DISPOSITION_MAX_CARACTERES = 10_000;

    private static final Set<String> WIDGETS_CONNUS = Arrays.stream(WidgetDashboard.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    private final DashboardService dashboardService;
    private final DashboardPreferenceService preferenceService;
    private final DashboardProperties props;

    public DashboardController(DashboardService dashboardService,
                               DashboardPreferenceService preferenceService,
                               DashboardProperties props) {
        this.dashboardService = dashboardService;
        this.preferenceService = preferenceService;
        this.props = props;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, Principal principal) {
        String user = principal.getName();
        Map<String, WidgetGeometry> layout = preferenceService.layout(user);

        List<WidgetAffichage> widgets = preferenceService.widgetsVisibles(user).stream()
                .map(w -> {
                    WidgetGeometry g = layout.get(w.name());
                    return new WidgetAffichage(w,
                            g != null ? g.x() : null,
                            g != null ? g.y() : null,
                            g != null ? g.w() : 4,   // largeur par défaut (sur 12 colonnes)
                            g != null ? g.h() : 3);  // hauteur par défaut (en lignes)
                }).toList();

        model.addAttribute("pageTitle", "Tableau de bord");
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("vue", dashboardService.build());
        model.addAttribute("widgets", widgets);
        return "dashboard";
    }

    @ResponseBody
    @PostMapping("/interne/dashboard/disposition")
    public ResponseEntity<Map<String, Object>> enregistrerDisposition(@RequestBody String layoutJson,
                                                                      Principal principal) {
        String probleme = verifierDisposition(layoutJson);
        if (probleme != null) {
            log.warn("Disposition du tableau de bord refusée pour l'utilisateur {} : {}",
                    principal.getName(), probleme);
            HttpStatus statut = (layoutJson != null && layoutJson.length() > DISPOSITION_MAX_CARACTERES)
                    ? HttpStatus.PAYLOAD_TOO_LARGE
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(statut).body(Map.of("erreur", probleme));
        }

        try {
            preferenceService.enregistrerLayout(principal.getName(), layoutJson);
            log.info("Disposition du tableau de bord modifiée par l'utilisateur {}", principal.getName());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Échec de l'enregistrement de la disposition du tableau de bord de l'utilisateur {}",
                    principal.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erreur", "La disposition n'a pas pu être enregistrée."));
        }
    }

    /**
     * Vérifie la taille et la structure du JSON envoyé par Gridstack :
     * un tableau d'objets {id, x, y, w, h}, un par widget connu, sans doublon.
     * Renvoie null si tout est valide, sinon le message d'erreur.
     */
    private String verifierDisposition(String json) {
        if (json == null || json.isBlank()) {
            return "La disposition est vide.";
        }
        if (json.length() > DISPOSITION_MAX_CARACTERES) {
            return "La disposition est trop volumineuse.";
        }

        JsonNode racine;
        try {
            racine = JSON.readTree(json);
        } catch (JsonProcessingException e) {
            return "La disposition n'est pas un JSON valide.";
        }

        if (racine == null || !racine.isArray()) {
            return "La disposition doit être un tableau JSON.";
        }
        if (racine.size() > WIDGETS_CONNUS.size()) {
            return "La disposition contient trop d'éléments.";
        }

        Set<String> dejaVus = new HashSet<>();
        for (JsonNode noeud : racine) {
            if (!noeud.isObject()) {
                return "Chaque élément de la disposition doit être un objet.";
            }
            JsonNode idNoeud = noeud.get("id");
            if (idNoeud == null || !idNoeud.isTextual() || !WIDGETS_CONNUS.contains(idNoeud.asText())) {
                return "La disposition contient un widget inconnu.";
            }
            String id = idNoeud.asText();
            if (!dejaVus.add(id)) {
                return "La disposition contient un widget en double.";
            }
            if (!entierValide(noeud, "x", 0, 11) || !entierValide(noeud, "y", 0, 1000)
                    || !entierValide(noeud, "w", 1, 12) || !entierValide(noeud, "h", 1, 50)) {
                return "Position ou taille invalide pour le widget " + id + ".";
            }
        }
        return null;
    }

    /** Gridstack n'envoie pas les valeurs par défaut (par exemple w=1) : un champ absent est accepté. */
    private static boolean entierValide(JsonNode noeud, String champ, int min, int max) {
        JsonNode valeur = noeud.get(champ);
        if (valeur == null) {
            return true;
        }
        return valeur.isInt() && valeur.intValue() >= min && valeur.intValue() <= max;
    }

    @PostMapping("/dashboard/parametres")
    public String enregistrer(@RequestParam(name = "widgets", required = false) List<String> widgets,
                              Principal principal) {
        preferenceService.enregistrer(principal.getName(), widgets == null ? List.of() : widgets);
        return "redirect:/dashboard";
    }
}