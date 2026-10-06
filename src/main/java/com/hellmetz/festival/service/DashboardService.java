package com.hellmetz.festival.service;

import com.hellmetz.festival.dashboard.*;
import com.hellmetz.festival.model.*;
import com.hellmetz.festival.repository.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final int SEUIL_CHANGEOVER_MINUTES = 15; // en-deçà = alerte

    private final ArtisteRepository artisteRepo;
    private final EditionRepository editionRepo;
    private final ConcertRepository concertRepo;

    public DashboardService(ArtisteRepository artisteRepo,
                            EditionRepository editionRepo,
                            ConcertRepository concertRepo) {
        this.artisteRepo = artisteRepo;
        this.editionRepo = editionRepo;
        this.concertRepo = concertRepo;
    }

    public DashboardView build() {
        LocalDateTime maintenant = LocalDateTime.now();

        // --- Concerts du jour ---
        LocalDateTime debutJour = maintenant.toLocalDate().atStartOfDay();
        LocalDateTime finJour   = debutJour.plusDays(1);
        List<Concert> concertsDuJour = concertRepo.findByDateHeureDebutBetween(debutJour, finJour);

        // --- En cours / à venir ---
        List<Concert> enCours = concertsDuJour.stream()
                .filter(c -> !maintenant.isBefore(c.getDateHeureDebut())
                        && maintenant.isBefore(c.getDateHeureFin()))
                .toList();

        List<Concert> aVenir = concertsDuJour.stream()
                .filter(c -> c.getDateHeureDebut().isAfter(maintenant)
                        && c.getDateHeureDebut().isBefore(maintenant.plusHours(1)))
                .sorted(Comparator.comparing(Concert::getDateHeureDebut))
                .toList();

        // --- Taux d'occupation par scène (section 1) ---
        List<SceneOccupation> occupations = calculerOccupations(concertsDuJour);

        // --- Alertes changeover (section 1) ---
        List<ChangeoverAlert> alertes = calculerChangeovers(concertsDuJour);

        // --- Section 4 : édition courante ---
        EditionResume edition = editionRepo.findByActifTrue()
                .map(e -> resumeEdition(e, maintenant))
                .orElse(null);

        return new DashboardView(
                artisteRepo.count(),
                editionRepo.count(),
                edition,
                occupations,
                enCours,
                aVenir,
                alertes
        );
    }

    /** % du temps occupé par scène, rapporté à l'amplitude de la journée festival. */
    private List<SceneOccupation> calculerOccupations(List<Concert> concerts) {
        if (concerts.isEmpty()) return List.of();

        // Amplitude de référence = du premier début à la dernière fin, toutes scènes confondues.
        LocalDateTime ouverture = concerts.stream()
                .map(Concert::getDateHeureDebut).min(Comparator.naturalOrder()).orElseThrow();
        LocalDateTime fermeture = concerts.stream()
                .map(Concert::getDateHeureFin).max(Comparator.naturalOrder()).orElseThrow();
        long amplitudeMin = Duration.between(ouverture, fermeture).toMinutes();
        if (amplitudeMin <= 0) return List.of();

        // Regroupe par scène, somme les durées.
        Map<Scene, List<Concert>> parScene = concerts.stream()
                .filter(c -> c.getScene() != null)
                .collect(Collectors.groupingBy(Concert::getScene));

        return parScene.entrySet().stream()
                .map(e -> {
                    long occupeMin = e.getValue().stream()
                            .mapToLong(c -> Duration.between(
                                    c.getDateHeureDebut(), c.getDateHeureFin()).toMinutes())
                            .sum();
                    int pct = (int) Math.min(100, Math.round(occupeMin * 100.0 / amplitudeMin));
                    return new SceneOccupation(e.getKey().getNom(), pct);
                })
                .sorted(Comparator.comparing(SceneOccupation::nomScene))
                .toList();
    }

    /** Détecte les enchaînements trop serrés (ou les chevauchements) sur une même scène. */
    private List<ChangeoverAlert> calculerChangeovers(List<Concert> concerts) {
        List<ChangeoverAlert> alertes = new ArrayList<>();

        Map<Scene, List<Concert>> parScene = concerts.stream()
                .filter(c -> c.getScene() != null)
                .collect(Collectors.groupingBy(Concert::getScene));

        for (var entree : parScene.entrySet()) {
            List<Concert> liste = entree.getValue().stream()
                    .sorted(Comparator.comparing(Concert::getDateHeureDebut))
                    .toList();
            for (int i = 0; i < liste.size() - 1; i++) {
                Concert courant = liste.get(i);
                Concert suivant = liste.get(i + 1);
                long ecart = Duration.between(
                        courant.getDateHeureFin(), suivant.getDateHeureDebut()).toMinutes();
                if (ecart < SEUIL_CHANGEOVER_MINUTES) {
                    alertes.add(new ChangeoverAlert(
                            entree.getKey().getNom(),
                            libelle(courant), libelle(suivant), ecart));
                }
            }
        }
        return alertes;
    }

    private EditionResume resumeEdition(Edition e, LocalDateTime maintenant) {
        long jours = 0;
        if (e.getDateDebut() != null) {
            LocalDateTime ouverture = e.getDateDebut().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            jours = Math.max(0, ChronoUnit.DAYS.between(maintenant, ouverture));
        }
        List<Concert> concerts = concertRepo.findByEdition(e);
        int programmes = (int) concerts.stream()
                .filter(c -> "Programmé".equals(c.getStatut())).count();
        return new EditionResume(e.getNomEdition(), jours, programmes, concerts.size());
    }

    private String libelle(Concert c) {
        return "Concert #" + c.getId(); // adapte si tu as un nom de groupe lié
    }
}