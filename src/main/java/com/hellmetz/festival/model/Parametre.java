package com.hellmetz.festival.model;

import jakarta.persistence.*;

@Entity
@Table(name = "parametre" )
public class Parametre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametre")
    private Long idParametre;

    @Column(name = "code_parametre")
    private String codeParametre;

    @Column(name = "libelle_parametre")
    private String libelleParametre;

    @Column(name = "valeur_parametre")
    private String valeurParametre;




    public Parametre() {}




    public Long getIdParametre() { return idParametre;}
    public void setIdParametre(Long idParametre) { this.idParametre = idParametre; }

    public String getCodeParametre() {return codeParametre;}
    public void setCodeParametre(String codeParametre) { this.codeParametre = codeParametre; }

    public String getLibelleParametre() {return libelleParametre;}
    public void setLibelleParametre(String libelleParametre) { this.libelleParametre = libelleParametre; }

    public String getValeurParametre() {return valeurParametre;}
    public void setValeurParametre(String valeurParametre) { this.valeurParametre = valeurParametre; }



}
