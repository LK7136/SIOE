package com.hellmetz.festival.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "edition_festival")
public class Edition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_edition")
    private Long id;

    @Column(name = "annee")
    private int annee;

    @Column(name = "nom_edition")
    private String nomEdition;

    @Column(name = "date_debut")
    private Date dateDebut;

    @Column(name = "date_fin")
    private Date dateFin;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "lieu")
    private String lieu;

    @Column(name = "theme")
    private String theme;

    @Column(name = "url_affiche_officielle")
    private String urlAfficheOfficielle;

    @Column(name = "budget_previsionnel")
    private BigDecimal budgetPrevisionnel;

    @Column(name = "jauge_maximale")
    private int jaugeMaximale;

    @Column(name = "lattitude")
    private BigDecimal lattitude;

    @Column(name = "actif", nullable = false)
    private boolean actif;

    public Edition() {}

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public String getNomEdition() { return nomEdition; }
    public void setNomEdition(String nomEdition) { this.nomEdition = nomEdition; }

    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getUrlAfficheOfficielle() { return urlAfficheOfficielle; }
    public void setUrlAfficheOfficielle(String urlAfficheOfficielle) { this.urlAfficheOfficielle = urlAfficheOfficielle; }

    public BigDecimal getBudgetPrevisionnel() { return budgetPrevisionnel; }
    public void setBudgetPrevisionnel(BigDecimal budgetPrevisionnel) { this.budgetPrevisionnel = budgetPrevisionnel; }

    public int getJaugeMaximale() { return jaugeMaximale; }
    public void setJaugeMaximale(int jaugeMaximale) { this.jaugeMaximale = jaugeMaximale; }

    public BigDecimal getLattitude() { return lattitude; }
    public void setLattitude(BigDecimal lattitude) { this.lattitude = lattitude; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
