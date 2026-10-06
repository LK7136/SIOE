package com.hellmetz.festival.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dashboard")
public class DashboardProperties {
    private int changeoverSeuilMinutes = 15;
    private int apercuFenetreMinutes = 60;
    private int rafraichissementSecondes = 0; // 0 = désactivé

    public int getChangeoverSeuilMinutes() { return changeoverSeuilMinutes; }
    public void setChangeoverSeuilMinutes(int v) { this.changeoverSeuilMinutes = v; }
    public int getApercuFenetreMinutes() { return apercuFenetreMinutes; }
    public void setApercuFenetreMinutes(int v) { this.apercuFenetreMinutes = v; }
    public int getRafraichissementSecondes() { return rafraichissementSecondes; }
    public void setRafraichissementSecondes(int v) { this.rafraichissementSecondes = v; }
}