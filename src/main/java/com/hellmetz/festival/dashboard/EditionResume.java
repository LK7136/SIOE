package com.hellmetz.festival.dashboard;

public record EditionResume(
        String nom,
        long joursAvantOuverture,         // compte à rebours
        int concertsProgrammes,           // progression
        int concertsTotal
) {}