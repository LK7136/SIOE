package com.hellmetz.festival.dashboard;

public record ChangeoverAlert(
        String nomScene,
        String concertPrecedent,
        String concertSuivant,
        long minutesEntreLesDeux
) {}