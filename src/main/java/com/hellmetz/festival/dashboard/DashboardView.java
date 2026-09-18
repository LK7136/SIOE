package com.hellmetz.festival.dashboard;

import com.hellmetz.festival.model.Concert;
import java.util.List;

public record DashboardView(
        long nbArtistes,
        long nbEditions,
        EditionResume edition,            // section 4
        List<SceneOccupation> occupations,// section 1 - barres
        List<Concert> concertsEnCours,    // section 1
        List<Concert> concertsAVenir,     // section 1
        List<ChangeoverAlert> alertes     // section 1
) {}
