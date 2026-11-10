package com.hellmetz.festival.model;

import jakarta.persistence.*;  // Importez les annotations JPA/Hibernate
import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;



@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long id;

    @NotBlank(message = "L'identifiant est obligatoire")
    @Column(name = "identifiant", nullable = false, length = 100)
    private String identifiant;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "actif")
    private Boolean actif;

    @Column(name = "date_creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    @Column(name = "derniere_connexion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date derniereConnexion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_utilisateur",
            joinColumns = @JoinColumn(name = "id_utilisateur"),
            inverseJoinColumns = @JoinColumn(name = "id_role")
    )
    private List<Role> roles;

    /**
     * Permissions individuelles, en plus de celles portees par les roles.
     * Un Set (et non une List) : Hibernate refuse de charger deux List EAGER
     * dans la meme entite (MultipleBagFetchException).
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "permission_utilisateur",
            joinColumns = @JoinColumn(name = "id_utilisateur"),
            inverseJoinColumns = @JoinColumn(name = "id_permission")
    )
    private Set<Permission> permissions = new LinkedHashSet<>();




    // Getters et Setters à compléter


    public Utilisateur() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(Date derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }


    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /** Permissions individuelles uniquement (hors roles). */
    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Permissions effectives = permissions des roles UNION permissions individuelles.
     * Source unique de verite : utilisee a la fois pour les authorities Spring Security
     * et pour l'affichage de la fiche utilisateur.
     * Les permissions des roles (LAZY) doivent etre initialisees avant l'appel
     * si l'entite est detachee.
     */
    public Set<Permission> getPermissionsEffectives() {
        Set<Permission> effectives = new LinkedHashSet<>();
        if (roles != null) {
            for (Role role : roles) {
                if (role.getPermissions() != null) {
                    effectives.addAll(role.getPermissions());
                }
            }
        }
        if (permissions != null) {
            effectives.addAll(permissions);
        }
        return effectives;
    }


}











