package com.hellmetz.festival.dashboard;

public enum WidgetDashboard {
    OCCUPATION_SCENES ("Occupation des scènes",       "occupationScenes", 10),
    CONCERTS_EN_COURS ("Concerts en cours",           "concertsEnCours",  20),
    CONCERTS_A_VENIR  ("Concerts à venir",            "concertsAVenir",   30),
    ALERTES_CHANGEOVER("Alertes changement de plateau","alertesChangeover",40),
    COMPTE_A_REBOURS  ("Compte à rebours édition",     "compteARebours",   50),
    PROGRESSION       ("Progression programmation",    "progression",      60);

    private final String libelle;
    private final String fragment;
    private final int ordreParDefaut;

    WidgetDashboard(String libelle, String fragment, int ordreParDefaut) {
        this.libelle = libelle; this.fragment = fragment; this.ordreParDefaut = ordreParDefaut;
    }
    public String getLibelle() { return libelle; }
    public String getFragment() { return fragment; }
    public int getOrdreParDefaut() { return ordreParDefaut; }
}
