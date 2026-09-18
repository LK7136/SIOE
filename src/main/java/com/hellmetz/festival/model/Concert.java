package com.hellmetz.festival.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "concert")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_concert")
    private Long id;

    @Column(name = "statut")
    private String statut;

    @Column(name = "date_heure_debut")
    private LocalDateTime dateHeureDebut;

    @Column(name = "date_heure_fin")
    private LocalDateTime dateHeureFin;

    @Column(name = "heure_balance_debut")
    private LocalDateTime  heureBalanceDebut;

    @Column(name = "heure_balance_fin")
    private LocalDateTime heureBalanceFin;

    @Column(name = "decibels_max")
    private int decibelsMax;



    @ManyToOne
    @JoinColumn(name = "id_scene")
    private Scene scene;

    @ManyToOne
    @JoinColumn(name = "id_edition")
    private Edition edition;

    @ManyToOne
    @JoinColumn(name = "id_groupe")
    private Groupe groupe;

    public Concert(){}


    public Long getId() { return id; }
    public void setId(Long id) {this.id = id;}

    public String getStatut() { return statut; }
    public void setStatut(String statut) {this.statut = statut;}

    public LocalDateTime getDateHeureDebut() { return dateHeureDebut; }
    public void setDateHeureDebut(LocalDateTime dateHeureDebut) {this.dateHeureDebut = dateHeureDebut;}

    public LocalDateTime getDateHeureFin() { return dateHeureFin; }
    public void setDateHeureFin(LocalDateTime dateHeureFin) {this.dateHeureFin = dateHeureFin;}

    public LocalDateTime getHeureBalanceDebut() { return heureBalanceDebut; }
    public void setHeureBalanceDebut(LocalDateTime heureBalanceDebut) { this.heureBalanceDebut = heureBalanceDebut; }

    public LocalDateTime getHeureBalanceFin() { return heureBalanceFin; }
    public void setHeureBalanceFin(LocalDateTime heureBalanceFin) { this.heureBalanceFin = heureBalanceFin; }

    public int getDecibelsMax() { return decibelsMax; }
    public void setDecibelsMax(int decibelsMax) {this.decibelsMax = decibelsMax;}

    public Groupe getGroupe() { return groupe; }
    public void setGroupe(Groupe groupe) { this.groupe = groupe; }

    public Edition getEdition() { return edition; }
    public void setEdition(Edition edition) { this.edition = edition; }

    public Scene getScene() { return scene; }
    public void setScene(Scene scene) { this.scene = scene; }
}