package com.hellmetz.festival.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
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

    @ManyToMany(mappedBy = "styles")
    private List<Artiste> artistes;



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
    // Garde getStyles() et setStyles(...) pour ton formulaire multi-sélection
    public List<Style> getStyles() { return styles; }
    public void setStyles(List<Style> styles) { this.styles = styles; }

    // Compatibilité pour le reste de l'application (liste des artistes, etc.)
    public Style getStyle() {
        return (styles != null && !styles.isEmpty()) ? styles.get(0) : null;
    }

    public void setStyle(Style style) {
        if (this.styles == null) this.styles = new ArrayList<>();
        this.styles.clear();
        if (style != null) this.styles.add(style);
    }

}

