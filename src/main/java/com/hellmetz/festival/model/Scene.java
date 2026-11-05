package com.hellmetz.festival.model;

import jakarta.persistence.*;

@Entity
@Table(name = "scene")
public class Scene {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_scene")
    private Long id;

    @Column(name = "nom_scene")
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "capacite")
    private int capacite;

    @Column(name = "actif")
    private boolean actif;

    @Column(name = "type_scene")
    private String type;

    @Column(name = "superficie_m2")
    private int superficie;

    @Column(name = "url_plan_technique")
    private String urlPlanTechnique;

    public Scene() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public boolean getActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getSuperficie() { return superficie; }
    public void setSuperficie(int superficie) { this.superficie = superficie; }

    public String getUrlPlanTechnique() { return urlPlanTechnique; }
    public void setUrlPlanTechnique(String urlPlanTechnique) { this.urlPlanTechnique = urlPlanTechnique; }
}