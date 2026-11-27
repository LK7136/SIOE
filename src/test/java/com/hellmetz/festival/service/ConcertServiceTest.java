package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Concert;
import com.hellmetz.festival.model.Edition;
import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.repository.ConcertRepository;
import com.hellmetz.festival.repository.ParametreRepository;
import com.hellmetz.festival.service.ConcertService.FenetreFestival;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de ConcertService (sans base de données).
 * On teste : la fenêtre de dates du festival et la validation des horaires (concert + balance).
 */
@ExtendWith(MockitoExtension.class) // active Mockito avec JUnit 5
class ConcertServiceTest {

    @Mock
    ConcertRepository concertRepository;     // faux repository des concerts

    @Mock
    ParametreRepository parametreRepository; // faux repository des paramètres

    ConcertService concertService;           // le vrai service testé

    private Edition edition;

    @BeforeEach
    void preparerUneEdition() {
        // On crée le vrai service avec les faux repositories
        // (on simule les repositories et pas ParametreService, car Mockito
        //  ne sait pas simuler une classe avec Java 26)
        ParametreService parametreService = new ParametreService(parametreRepository);
        concertService = new ConcertService(concertRepository, parametreService);

        // Édition du 3 au 5 juillet 2026
        edition = new Edition();
        edition.setDateDebut(LocalDate.of(2026, 7, 3));
        edition.setDateFin(LocalDate.of(2026, 7, 5));
    }

    /** Crée un concert le 3 juillet 2026 avec les heures données. */
    private Concert creerConcert(String debut, String fin, String balanceDebut, String balanceFin) {
        Concert concert = new Concert();
        concert.setEdition(edition);
        concert.setDateHeureDebut(LocalDateTime.parse("2026-07-03T" + debut));
        concert.setDateHeureFin(LocalDateTime.parse("2026-07-03T" + fin));
        concert.setHeureBalanceDebut(LocalDateTime.parse("2026-07-03T" + balanceDebut));
        concert.setHeureBalanceFin(LocalDateTime.parse("2026-07-03T" + balanceFin));
        return concert;
    }

    /** Crée un paramètre avec une valeur. */
    private Parametre creerParametre(String valeur) {
        Parametre parametre = new Parametre();
        parametre.setValeurParametre(valeur);
        return parametre;
    }

    // ------------------------------------------------------------------
    // Fenêtre de dates du festival (getFenetre)
    // ------------------------------------------------------------------

    @Test
    void getFenetre_avecEdition_doitUtiliserLesDatesDeLEdition() {
        // Exécution
        Optional<FenetreFestival> fenetre = concertService.getFenetre(edition);

        // Vérification
        assertTrue(fenetre.isPresent());
        assertEquals(LocalDateTime.of(2026, 7, 3, 0, 0), fenetre.get().debut());
        assertEquals(LocalDateTime.of(2026, 7, 5, 23, 59), fenetre.get().fin());
    }

    @Test
    void getFenetre_sansEdition_doitUtiliserLesParametres() {
        // Préparation : les paramètres du festival contiennent des dates
        when(parametreRepository.findByCodeParametre("festival.date_debut")).thenReturn(creerParametre("2026-08-01"));
        when(parametreRepository.findByCodeParametre("festival.date_fin")).thenReturn(creerParametre("2026-08-02"));

        // Exécution
        Optional<FenetreFestival> fenetre = concertService.getFenetre(null);

        // Vérification
        assertTrue(fenetre.isPresent());
        assertEquals(LocalDateTime.of(2026, 8, 1, 0, 0), fenetre.get().debut());
        assertEquals(LocalDateTime.of(2026, 8, 2, 23, 59), fenetre.get().fin());
    }

    @Test
    void getFenetre_parametreAbsent_doitEtreVide() {
        // Préparation : rien à faire, le faux repository renvoie null (paramètre absent)

        // Exécution
        Optional<FenetreFestival> fenetre = concertService.getFenetre(null);

        // Vérification
        assertTrue(fenetre.isEmpty());
    }

    @Test
    void getFenetre_parametreInvalide_doitEtreVide() {
        // Préparation : la date de début n'est pas une date
        when(parametreRepository.findByCodeParametre("festival.date_debut")).thenReturn(creerParametre("pas-une-date"));
        when(parametreRepository.findByCodeParametre("festival.date_fin")).thenReturn(creerParametre("2026-08-02"));

        // Exécution
        Optional<FenetreFestival> fenetre = concertService.getFenetre(null);

        // Vérification
        assertTrue(fenetre.isEmpty());
    }

    // ------------------------------------------------------------------
    // Validation des horaires (validerDates)
    // ------------------------------------------------------------------

    @Test
    void validerDates_concertEtBalanceCorrects_aucuneErreur() {
        // Préparation : cas nominal, balance 17h-18h, concert 20h-21h30
        Concert concert = creerConcert("20:00", "21:30", "17:00", "18:00");

        // Exécution
        Map<String, String> erreurs = concertService.validerDates(concert);

        // Vérification
        assertTrue(erreurs.isEmpty());
    }

    @Test
    void validerDates_finAvantDebut_doitDonnerUneErreur() {
        Concert concert = creerConcert("20:00", "19:00", "17:00", "18:00");

        Map<String, String> erreurs = concertService.validerDates(concert);

        assertTrue(erreurs.containsKey("dateHeureFin"));
    }

    @Test
    void validerDates_balanceApresLeDebutDuConcert_doitDonnerUneErreur() {
        // La balance commence à 20h30, alors que le concert commence à 20h
        Concert concert = creerConcert("20:00", "21:30", "20:30", "21:00");

        Map<String, String> erreurs = concertService.validerDates(concert);

        assertTrue(erreurs.containsKey("heureBalanceDebut"));
    }

    @Test
    void validerDates_balanceQuiDepasseLeDebutDuConcert_doitDonnerUneErreur() {
        // La balance finit à 20h30, alors que le concert commence à 20h
        Concert concert = creerConcert("20:00", "21:30", "19:00", "20:30");

        Map<String, String> erreurs = concertService.validerDates(concert);

        assertTrue(erreurs.containsKey("heureBalanceFin"));
    }

    @Test
    void validerDates_finDeBalanceAvantDebutDeBalance_doitDonnerUneErreur() {
        Concert concert = creerConcert("20:00", "21:30", "18:00", "17:00");

        Map<String, String> erreurs = concertService.validerDates(concert);

        assertTrue(erreurs.containsKey("heureBalanceFin"));
    }

    @Test
    void validerDates_balanceForceeHorsDuFestival_doitDonnerUneErreur() {
        // Préparation : balance forcée le 2 juillet, alors que le festival commence le 3
        Concert concert = creerConcert("20:00", "21:30", "17:00", "18:00");
        concert.setHeureBalanceDebut(LocalDateTime.of(2026, 7, 2, 17, 0));

        // Exécution
        Map<String, String> erreurs = concertService.validerDates(concert);

        // Vérification
        assertTrue(erreurs.containsKey("heureBalanceDebut"));
    }

    @Test
    void validerDates_sansEditionEtParametreAbsent_pasDeControleDesDatesDuFestival() {
        // Préparation : pas d'édition, pas de paramètre → aucune fenêtre à respecter
        Concert concert = creerConcert("20:00", "21:30", "17:00", "18:00");
        concert.setEdition(null);

        // Exécution
        Map<String, String> erreurs = concertService.validerDates(concert);

        // Vérification : les horaires sont cohérents, donc aucune erreur
        assertTrue(erreurs.isEmpty());
    }

    @Test
    void validerDates_sansEditionEtParametreInvalide_pasDeControleDesDatesDuFestival() {
        // Préparation : pas d'édition, et un paramètre de date invalide
        when(parametreRepository.findByCodeParametre("festival.date_debut")).thenReturn(creerParametre("abc"));
        when(parametreRepository.findByCodeParametre("festival.date_fin")).thenReturn(creerParametre("2026-07-05"));
        Concert concert = creerConcert("20:00", "21:30", "17:00", "18:00");
        concert.setEdition(null);

        // Exécution
        Map<String, String> erreurs = concertService.validerDates(concert);

        // Vérification : le paramètre invalide est ignoré, pas d'erreur
        assertTrue(erreurs.isEmpty());
    }
}
