package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Utilisateur;
import com.hellmetz.festival.repository.UtilisateurRepository;
import com.hellmetz.festival.security.UtilisateurDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service de gestion des utilisateurs.
 * Implemente UserDetailsService pour brancher l'authentification Spring Security
 * sur la table "utilisateur".
 */

@Service
@Transactional
public class UtilisateurDetailService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurDetailService.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Appele par Spring Security lors de la connexion.
     * Charge l'utilisateur et l'emballe dans un UtilisateurDetails.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifiant) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByIdentifiant(identifiant)
                .orElseThrow(() -> {
                    log.warn("Connexion refusée : identifiant '{}' introuvable", identifiant);
                    return new UsernameNotFoundException("Utilisateur introuvable : " + identifiant);
                });
        // Initialise les roles (LAZY) tant que la transaction est ouverte
        if (utilisateur.getRoles() != null) {
            utilisateur.getRoles().size();
        }
        log.debug("Utilisateur {} chargé pour l'authentification", utilisateur.getId());
        return new UtilisateurDetails(utilisateur);
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> findAll() {
        log.debug("Recherche de tous les utilisateurs");
        return utilisateurRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Utilisateur findById(Long id) {
        log.debug("Recherche de l'utilisateur {}", id);
        Utilisateur utilisateur = utilisateurRepository.findById(id).orElse(null);
        if (utilisateur == null) {
            log.warn("Utilisateur {} introuvable", id);
        }
        return utilisateur;
    }

    /** Cree un nouvel utilisateur avec mot de passe encode. */
    public void creerUtilisateur(Utilisateur utilisateur) {
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateur.setActif(true);
        utilisateur.setDateCreation(new Date());
        utilisateurRepository.save(utilisateur);
        log.info("Utilisateur {} créé", utilisateur.getId());
    }

    public void save(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
        log.info("Utilisateur {} enregistré", utilisateur.getId());
    }

    public void deleteById(Long id) {
        utilisateurRepository.deleteById(id);
        log.info("Utilisateur {} supprimé", id);
    }

    @Transactional(readOnly = true)
    public boolean isAdmin(Utilisateur utilisateur) {
        if (utilisateur.getRoles() == null) return false;
        return utilisateur.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getCodeRole()));
    }

    @Transactional(readOnly = true)
    public List<String> getCodesRoles(Utilisateur utilisateur) {
        return utilisateur.getRoles().stream()
                .map(role -> role.getCodeRole())
                .collect(Collectors.toList());
    }
}