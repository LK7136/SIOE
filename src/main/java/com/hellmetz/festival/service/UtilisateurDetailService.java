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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service de gestion des utilisateurs.
 * Implemente UserDetailsService pour brancher l'authentification Spring Security
 * sur la table "utilisateur".
 */

@Service
@Transactional
public class UtilisateurDetailService implements UserDetailsService {

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
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur introuvable : " + identifiant));
        // Initialise les roles (LAZY) tant que la transaction est ouverte
        if (utilisateur.getRoles() != null) {
            utilisateur.getRoles().size();
        }
        return new UtilisateurDetails(utilisateur);
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    /** Cree un nouvel utilisateur avec mot de passe encode. */
    public void creerUtilisateur(Utilisateur utilisateur) {
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateur.setActif(true);
        utilisateur.setDateCreation(new Date());
        utilisateurRepository.save(utilisateur);
    }

    public void save(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
    }

    public void deleteById(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public void enregistrerPhoto(Utilisateur utilisateur, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        try {
            String original = file.getOriginalFilename();
            String extension = ".png";
            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            }
            utilisateur.setUrlPhoto("user_" + UUID.randomUUID().toString().substring(0, 8) + extension);
            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "image/png";
            }
            utilisateur.setPhotoContentType(contentType);
            utilisateur.setContenuPhoto(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la photo", e);
        }
    }

    public void supprimerPhoto(Utilisateur utilisateur) {
        utilisateur.setUrlPhoto(null);
        utilisateur.setPhotoContentType(null);
        utilisateur.setContenuPhoto(null);
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
