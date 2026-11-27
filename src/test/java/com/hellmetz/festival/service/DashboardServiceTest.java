package com.hellmetz.festival.service;

import com.hellmetz.festival.dashboard.ChangeoverAlert;
import com.hellmetz.festival.model.Concert;
import com.hellmetz.festival.model.Scene;
import com.hellmetz.festival.repository.ArtisteRepository;
import com.hellmetz.festival.repository.ConcertRepository;
import com.hellmetz.festival.repository.EditionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de la détection de chevauchement des concerts (DashboardService).
 * Règle : sur une même scène, il faut au moins 15 minutes entre deux concerts,
 * sinon une alerte est créée (un chevauchement donne un écart négatif).
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    ArtisteRepository artisteRepository;

    @Mock
    EditionRepository editionRepository;

    @Mock
    ConcertRepository concertRepository;

    @InjectMocks
    DashboardService dashboardService;

    private Scene mainStage;

    @BeforeEach
    void preparerUneScene() {
        mainStage = new Scene();
        mainStage.setNom("Main Stage");
    }

    /** Crée un concert aujourd'hui, sur une scène, avec les heures données. */
    private Concert creerConcert(Long id, Scene scene, String debut, String fin) {
        Concert concert = new Concert();
        concert.setId(id);
        concert.setScene(scene);
        concert.setDateHeureDebut(LocalDate.now().atTime(LocalTime.parse(debut)));
        concert.setDateHeureFin(LocalDate.now().atTime(LocalTime.parse(fin)));
        return concert;
    }

    /** Le faux repository renvoie ces concerts comme "concerts du jour". */
    private List<ChangeoverAlert> alertesPour(Concert... concerts) {
        when(concertRepository.findByDateHeureDebutBetween(any(), any())).thenReturn(List.of(concerts));
        return dashboardService.build().alertes();
    }

    @Test
    void deuxConcertsQuiSeChevauchent_doiventDonnerUneAlerte() {
        // Préparation : le 2e concert commence avant la fin du 1er
        Concert premier = creerConcert(1L, mainStage, "14:00", "15:00");
        Concert second = creerConcert(2L, mainStage, "14:30", "15:30");

        // Exécution
        List<ChangeoverAlert> alertes = alertesPour(premier, second);

        // Vérification : une alerte, avec un écart de -30 minutes
        assertEquals(1, alertes.size());
        assertEquals(-30, alertes.get(0).minutesEntreLesDeux());
    }

    @Test
    void enchainementDe10Minutes_doitDonnerUneAlerte() {
        Concert premier = creerConcert(1L, mainStage, "14:00", "15:00");
        Concert second = creerConcert(2L, mainStage, "15:10", "16:00");

        List<ChangeoverAlert> alertes = alertesPour(premier, second);

        assertEquals(1, alertes.size());
        assertEquals(10, alertes.get(0).minutesEntreLesDeux());
    }

    @Test
    void enchainementDe30Minutes_neDoitPasDonnerDAlerte() {
        Concert premier = creerConcert(1L, mainStage, "14:00", "15:00");
        Concert second = creerConcert(2L, mainStage, "15:30", "16:30");

        List<ChangeoverAlert> alertes = alertesPour(premier, second);

        assertTrue(alertes.isEmpty());
    }

    @Test
    void chevauchementSurDeuxScenesDifferentes_neDoitPasDonnerDAlerte() {
        // Préparation : même horaire, mais sur deux scènes différentes
        Scene autreScene = new Scene();
        autreScene.setNom("Second Stage");
        Concert premier = creerConcert(1L, mainStage, "14:00", "15:00");
        Concert second = creerConcert(2L, autreScene, "14:30", "15:30");

        // Exécution
        List<ChangeoverAlert> alertes = alertesPour(premier, second);

        // Vérification
        assertTrue(alertes.isEmpty());
    }

    @Test
    void aucunConcert_neDoitPasDonnerDAlerte() {
        List<ChangeoverAlert> alertes = alertesPour();

        assertTrue(alertes.isEmpty());
    }
}
