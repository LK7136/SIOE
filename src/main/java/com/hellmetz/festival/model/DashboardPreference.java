package com.hellmetz.festival.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dashboard_preference")
public class DashboardPreference {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "widgets_visibles", length = 2000)
    private String widgetsVisibles; // ex: "OCCUPATION_SCENES,CONCERTS_EN_COURS,PROGRESSION"

    @Column(name = "layout", columnDefinition = "text")
    private String layout; // JSON Gridstack : [{"id":"...","x":0,"y":0,"w":6,"h":3}, ...]

    public DashboardPreference() {}
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getWidgetsVisibles() { return widgetsVisibles; }
    public void setWidgetsVisibles(String v) { this.widgetsVisibles = v; }
    public String getLayout() { return layout; }
    public void setLayout(String layout) { this.layout = layout; }
}