package com.hellmetz.festival.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "groupe")
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_groupe")
    private Long id;

    @Column(name = "nom_groupe")
    private String nomGroupe;

    @Column(name = "description")
    private String description;

    @Column(name = "actif")
    private Boolean actif;

    @Column(name = "annee_creation")
    private Integer anneeCreation;

    @Column(name = "ville_origine")
    private String villeOrigine;

    @Column(name = "pays_origine")
    private String paysOrigine;

    @Column(name = "url_logo")
    private String urlLogo;

    @Column(name = "site_web")
    private String siteWeb;

    @Column(name = "url_facebook")
    private String urlFacebook;

    @Column(name = "url_instagram")
    private String urlInstagram;

    @Column(name = "url_youtube")
    private String urlYoutube;

    @Column(name = "url_spotify")
    private String urlSpotify;

    @Column(name = "email_contact")
    private String emailContact;

    @Column(name = "telephone_contact")
    private String telephoneContact;

    @Column(name = "url_fiche_technique")
    private String urlFicheTechnique;

    @OneToMany(mappedBy = "groupe")
    private List<Concert> concerts;



    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "style_groupe",
            joinColumns = @JoinColumn(name = "id_groupe"),
            inverseJoinColumns = @JoinColumn(name = "id_style")
    )
    private Set<Style> stylesDuGroupe = new HashSet<>();




    public Groupe() {
    }




    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomGroupe() { return nomGroupe; }
    public void setNomGroupe(String nomGroupe) { this.nomGroupe = nomGroupe; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public Integer getAnneeCreation() { return anneeCreation; }
    public void setAnneeCreation(Integer anneeCreation) { this.anneeCreation = anneeCreation; }

    public String getVilleOrigine() { return villeOrigine; }
    public void setVilleOrigine(String villeOrigine) { this.villeOrigine = villeOrigine; }

    public String getPaysOrigine() { return paysOrigine; }
    public void setPaysOrigine(String paysOrigine) { this.paysOrigine = paysOrigine; }

    public String getUrlLogo() { return urlLogo; }
    public void setUrlLogo(String urlLogo) { this.urlLogo = urlLogo; }

    public String getSiteWeb() { return siteWeb; }
    public void setSiteWeb(String siteWeb) { this.siteWeb = siteWeb; }

    public String getUrlFacebook() { return urlFacebook; }
    public void setUrlFacebook(String urlFacebook) { this.urlFacebook = urlFacebook; }

    public String getUrlInstagram() { return urlInstagram; }
    public void setUrlInstagram(String urlInstagram) { this.urlInstagram = urlInstagram; }

    public String getUrlYoutube() { return urlYoutube; }
    public void setUrlYoutube(String urlYoutube) { this.urlYoutube = urlYoutube; }

    public String getUrlSpotify() { return urlSpotify; }
    public void setUrlSpotify(String urlSpotify) { this.urlSpotify = urlSpotify; }

    public String getEmailContact() { return emailContact; }
    public void setEmailContact(String emailContact) { this.emailContact = emailContact; }

    public String getTelephoneContact() { return telephoneContact; }
    public void setTelephoneContact(String telephoneContact) { this.telephoneContact = telephoneContact; }

    public String getUrlFicheTechnique() { return urlFicheTechnique; }
    public void setUrlFicheTechnique(String urlFicheTechnique) { this.urlFicheTechnique = urlFicheTechnique; }

    public List<Concert> getConcerts() { return concerts; }
    public void setConcerts(List<Concert> concerts) { this.concerts = concerts; }

    public void addStyles(Style style) {
        if (this.stylesDuGroupe == null) { this.stylesDuGroupe = new HashSet<>(); }
        this.stylesDuGroupe.add(style);
    }

    public Set<Style> getStylesDuGroupe() { return stylesDuGroupe; }
    public void setStylesDuGroupe(Set<Style> stylesDuGroupe) { this.stylesDuGroupe = stylesDuGroupe; }

}