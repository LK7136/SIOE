package com.hellmetz.festival.dashboard;

public record SceneOccupation(
        String nomScene,
        int pourcentage                   // 0 à 100, pour la largeur de la barre
) {}