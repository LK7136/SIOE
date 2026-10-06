package com.hellmetz.festival.model;

import jakarta.persistence.*;  // Importez les annotations JPA/Hibernate
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "artiste")
public class Artiste {

    // Question : que signifie GenerationType.IDENTITY ?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_artiste")
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "nom_scene")
    private String nomScene;

    @Column(name = "biographie")
    private String biographie;

    @Column(name = "url_photo")
    private String urlPhoto;

    @Column(name = "nationalite")
    private String nationalite;

    @Column(name = "cachet")
    private BigDecimal cachet;

    @Column(name = "url_facebook")
    private String urlFacebook;

    @Column(name = "url_instagram")
    private String urlInstagram;

    @Column(name = "url_spotify")
    private String urlSpotify;

    @Column(name = "exigences_catering")
    private String exigencesCatering;


    // Relation vers style
    @ManyToOne
    @JoinColumn(name = "id_style") // clé étrangère en BDD
    private Style style;

    // Relation avec Groupe
    @ManyToOne
    @JoinColumn(name = "id_groupe")
    private Groupe groupe;

    //

    public Artiste() {}  // Constructeur vide obligatoire pour Hibernate

    //getters et setters

    public Long getId() { return id;}

    public void setId(Long id) { this.id = id;}

    public String getNom() {return nom;}

    public void setNom(String nom) {this.nom = nom;}

    public String getPrenom() {return prenom;}

    public void setPrenom(String prenom) { this.prenom = prenom;}

    public String getNomScene() {return nomScene;}

    public void setNomScene(String nomScene) { this.nomScene = nomScene;}

    public String getBiographie() {return biographie;}

    public void setBiographie(String biographie) { this.biographie = biographie;}

    public String getUrlPhoto() {return urlPhoto;}

    public void setUrlPhoto(String urlPhoto) {this.urlPhoto = urlPhoto;}

    public String getNationalite() {return nationalite;}

    public void setNationalite(String nationalite) {this.nationalite = nationalite;}

    public BigDecimal getCachet() {return cachet;}

    public void setCachet(BigDecimal cachet) {this.cachet = cachet;}

    public String getUrlFacebook() {return urlFacebook;}

    public void setUrlFacebook(String urlFacebook) {this.urlFacebook = urlFacebook;}

    public String getUrlInstagram() {return urlInstagram;}

    public void setUrlInstagram(String urlInstagram) {this.urlInstagram = urlInstagram;}

    public String getUrlSpotify() {return urlSpotify;}

    public void setUrlSpotify(String urlSpotify) {this.urlSpotify = urlSpotify;}

    public String getExigencesCatering() {return exigencesCatering;}

    public void setExigencesCatering(String exigencesCatering) {this.exigencesCatering = exigencesCatering;}

    public Groupe getGroupe() {return groupe; }

    public void setGroupe(Groupe groupe) { this.groupe = groupe; }

    public Style getStyle() { return style; }

    public void setStyle(Style style) { this.style = style; }

}
