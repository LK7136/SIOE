package com.hellmetz.festival.model;

import com.hellmetz.festival.validation.Creation;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long id;

    @NotBlank(message = "L'identifiant est obligatoire")
    @Column(name = "identifiant", nullable = false, length = 100, unique = true)
    private String identifiant;

    // Règles appliquées seulement à la création (à la modification, le champ peut rester vide)
    @NotBlank(message = "Le mot de passe est obligatoire", groups = Creation.class)
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères", groups = Creation.class)
    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'adresse e-mail est invalide")
    @Column(name = "email", nullable = false, length = 100, unique = true)
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

    public Utilisateur() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIdentifiant() { return identifiant; }
    public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Date getDerniereConnexion() { return derniereConnexion; }
    public void setDerniereConnexion(Date derniereConnexion) { this.derniereConnexion = derniereConnexion; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }
}