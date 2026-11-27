package com.hellmetz.festival.controller;

import com.hellmetz.festival.config.SecurityConfig;
import com.hellmetz.festival.config.StorageProperties;
import com.hellmetz.festival.repository.EditionRepository;
import com.hellmetz.festival.repository.ParametreRepository;
import com.hellmetz.festival.repository.SceneRepository;
import com.hellmetz.festival.repository.StyleRepository;
import com.hellmetz.festival.repository.UtilisateurRepository;
import com.hellmetz.festival.service.EditionService;
import com.hellmetz.festival.service.ParametreService;
import com.hellmetz.festival.service.SceneService;
import com.hellmetz.festival.service.StyleService;
import com.hellmetz.festival.service.UtilisateurDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de tranche web : on charge seulement la partie web (contrôleur + sécurité),
 * sans base de données. On vérifie qui a le droit d'accéder à quelles URL.
 */
@WebMvcTest(ParametreController.class)
// On charge nos vraies règles de sécurité et les vrais services
@Import({SecurityConfig.class, StorageProperties.class,
        ParametreService.class, EditionService.class, StyleService.class,
        SceneService.class, UtilisateurDetailService.class})
class SecuriteTest {

    @Autowired
    MockMvc mockMvc; // simule le navigateur

    // Faux repositories (pas de base de données).
    // On simule les repositories et pas les services, car Mockito
    // ne sait pas simuler une classe avec Java 26.
    @MockBean ParametreRepository parametreRepository;
    @MockBean EditionRepository editionRepository;
    @MockBean UtilisateurRepository utilisateurRepository;
    @MockBean StyleRepository styleRepository;
    @MockBean SceneRepository sceneRepository;

    // ------------------------------------------------------------------
    // Page des paramètres : ADMIN et ORGANISATEUR seulement
    // ------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void parametres_admin_doitEtreAutorise() throws Exception {
        mockMvc.perform(get("/parametres/liste"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void parametres_organisateur_doitEtreAutorise() throws Exception {
        mockMvc.perform(get("/parametres/liste"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void parametres_simpleUtilisateur_doitEtreRefuse() throws Exception {
        mockMvc.perform(get("/parametres/liste"))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    void parametres_nonConnecte_doitEtreRedirigeVersLogin() throws Exception {
        mockMvc.perform(get("/parametres/liste"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // ------------------------------------------------------------------
    // Suppression : réservée à l'ADMIN
    // ------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ORGANISATEUR")
    void suppression_organisateur_doitEtreRefusee() throws Exception {
        mockMvc.perform(get("/concerts/delete/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void suppression_simpleUtilisateur_doitEtreRefusee() throws Exception {
        mockMvc.perform(get("/groupes/delete/1"))
                .andExpect(status().isForbidden());
    }
}
