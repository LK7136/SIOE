package com.hellmetz.festival.security;

import com.hellmetz.festival.model.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptateur entre l'entite metier {@link Utilisateur} et l'interface
 * {@link UserDetails} de Spring Security.
 *
 * Grace a cette classe, l'utilisateur connecte (avec tous ses champs metier :
 * prenom, nom, email...) est accessible partout via
 * {@code authentication.getPrincipal()}, sans requete BDD supplementaire.
 */
public class UtilisateurDetails implements UserDetails {

    private final Utilisateur utilisateur;

    public UtilisateurDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    /** Donne acces a l'entite metier complete (utilise dans les templates). */
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    // --- Raccourcis pratiques pour les templates Thymeleaf ---

    public String getPrenom() {
        return utilisateur.getPrenom();
    }

    public String getNom() {
        return utilisateur.getNom();
    }

    public boolean isAdmin() {
        // L'admin se determine par la presence du role ADMIN (pas de champ is_admin en base)
        if (utilisateur.getRoles() == null) return false;
        return utilisateur.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getCodeRole()));
    }

    // --- Implementation de UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Construit les roles Spring Security a partir des roles metier.
        // Convention Spring : prefixe "ROLE_".
        if (utilisateur.getRoles() == null) {
            return List.of();
        }
        return utilisateur.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getCodeRole()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse();
    }

    @Override
    public String getUsername() {
        return utilisateur.getIdentifiant();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Compte desactive si actif == false
        return utilisateur.getActif() == null || utilisateur.getActif();
    }
}
