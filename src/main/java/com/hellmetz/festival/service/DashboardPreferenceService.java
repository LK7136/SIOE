package com.hellmetz.festival.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellmetz.festival.dashboard.WidgetDashboard;
import com.hellmetz.festival.dashboard.WidgetGeometry;
import com.hellmetz.festival.model.DashboardPreference;
import com.hellmetz.festival.repository.DashboardPreferenceRepository;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
import java.util.stream.*;

@Service
public class DashboardPreferenceService {

    private final DashboardPreferenceRepository repo;
    private final ObjectMapper json = new ObjectMapper();

    public DashboardPreferenceService(DashboardPreferenceRepository repo) { this.repo = repo; }

    public void enregistrerLayout(String username, String layoutJson) {
        DashboardPreference p = repo.findById(username).orElseGet(DashboardPreference::new);
        p.setUsername(username);
        p.setLayout(layoutJson);
        repo.save(p);
    }

    public Map<String, WidgetGeometry> layout(String username) {
        return repo.findById(username)
                .map(DashboardPreference::getLayout)
                .filter(s -> s != null && !s.isBlank())
                .map(this::parseLayout)
                .orElseGet(Map::of);
    }

    private Map<String, WidgetGeometry> parseLayout(String contenu) {
        try {
            List<Map<String, Object>> noeuds =
                    json.readValue(contenu, new TypeReference<List<Map<String, Object>>>() {});
            Map<String, WidgetGeometry> map = new HashMap<>();
            for (Map<String, Object> n : noeuds) {
                Object id = n.get("id");
                if (id == null) continue;
                map.put(id.toString(), new WidgetGeometry(
                        asInt(n.get("x")), asInt(n.get("y")), asInt(n.get("w")), asInt(n.get("h"))));
            }
            return map;
        } catch (Exception e) {
            return Map.of(); // layout corrompu -> on repart sur le placement auto
        }
    }

    private Integer asInt(Object o) { return (o instanceof Number num) ? num.intValue() : null; }

    /** Widgets visibles, ordonnés, pour un utilisateur (défaut si aucune préférence). */
    public List<WidgetDashboard> widgetsVisibles(String username) {
        return repo.findById(username)
                .map(p -> parse(p.getWidgetsVisibles()))
                .filter(l -> !l.isEmpty())
                .orElseGet(this::parDefaut);
    }

    public void enregistrer(String username, List<String> idsOrdonnes) {
        DashboardPreference p = repo.findById(username).orElseGet(DashboardPreference::new);
        p.setUsername(username);
        p.setWidgetsVisibles(String.join(",", idsOrdonnes));
        repo.save(p);
    }

    public List<WidgetDashboard> parDefaut() {
        return Arrays.stream(WidgetDashboard.values())
                .sorted(Comparator.comparingInt(WidgetDashboard::getOrdreParDefaut))
                .toList();
    }

    private List<WidgetDashboard> parse(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(this::versWidget)         // ignore les identifiants inconnus
                .filter(Objects::nonNull)
                .toList();
    }

    private WidgetDashboard versWidget(String id) {
        try { return WidgetDashboard.valueOf(id); }
        catch (IllegalArgumentException e) { return null; } // widget supprimé du catalogue
    }
}