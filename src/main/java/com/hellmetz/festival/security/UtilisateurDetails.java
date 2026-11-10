package com.hellmetz.festival.security;

import com.hellmetz.festival.model.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    /** Roles (ROLE_xxx) + permissions effectives, calcules une fois a la connexion. */
    private final Set<GrantedAuthority> authorities;

    public UtilisateurDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        // Calcule ici (transaction de loadUserByUsername encore ouverte) pour eviter
        // toute LazyInitializationException plus tard.
        Set<GrantedAuthority> auths = new LinkedHashSet<>();
        if (utilisateur.getRoles() != null) {
            utilisateur.getRoles().forEach(role ->
                    auths.add(new SimpleGrantedAuthority("ROLE_" + role.getCodeRole())));
        }
        utilisateur.getPermissionsEffectives().forEach(p -> {
            if (p.getCodePermission() != null) {
                auths.add(new SimpleGrantedAuthority(p.getCodePermission()));
            }
        });
        this.authorities = Set.copyOf(auths);
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
        // Roles : prefixe "ROLE_" (convention Spring) ; permissions : code brut (ex. ADD_GRP),
        // a tester avec hasAuthority('ADD_GRP').
        return authorities;
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
