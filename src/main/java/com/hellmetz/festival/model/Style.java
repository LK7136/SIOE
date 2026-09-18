package com.hellmetz.festival.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "style")
public class Style {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_style")
    private Long id;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "description")
    private String description;






    public Style() {}

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Style)) return false;
        Style style = (Style) o;
        return id != null && id.equals(style.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}

