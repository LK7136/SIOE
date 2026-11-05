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

@Controller
public class DashboardController {

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

    @PostMapping("/dashboard/disposition")
    @ResponseBody
    public void enregistrerDisposition(@RequestBody String layoutJson, Principal principal) {
        preferenceService.enregistrerLayout(principal.getName(), layoutJson);
    }


    @GetMapping("/dashboard/parametres")
    public String parametres(Model model, Principal principal) {
        List<WidgetDashboard> visibles = preferenceService.widgetsVisibles(principal.getName());
        List<WidgetDashboard> reste = preferenceService.parDefaut().stream()
                .filter(w -> !visibles.contains(w)).toList();
        List<WidgetDashboard> affichage = new ArrayList<>(visibles);
        affichage.addAll(reste);

        model.addAttribute("affichage", affichage);
        model.addAttribute("visibles", visibles);
        model.addAttribute("pageTitle", "Personnaliser le tableau de bord");
        model.addAttribute("activeMenu", "dashboard");
        return "dashboard/parametres";
    }

    @PostMapping("/dashboard/parametres")
    public String enregistrer(@RequestParam(name = "widgets", required = false) List<String> widgets,
                              Principal principal) {
        preferenceService.enregistrer(principal.getName(), widgets == null ? List.of() : widgets);
        return "redirect:/dashboard";
    }
}