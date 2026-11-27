package com.hellmetz.festival.service;

import com.hellmetz.festival.repository.ParametreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de ParametreService (sans base de données).
 * On vérifie les valeurs par défaut quand un paramètre est absent ou invalide.
 */
@ExtendWith(MockitoExtension.class)
class ParametreServiceTest {

    @Mock
    ParametreRepository parametreRepository;

    @InjectMocks
    ParametreService parametreService;

    @Test
    void getLogoUrl_sansLogo_doitRenvoyerLeLogoParDefaut() {
        // Préparation : rien, le faux repository renvoie null (pas de logo en base)

        // Exécution
        String url = parametreService.getLogoUrl();

        // Vérification
        assertEquals(ParametreService.DEFAULT_LOGO_URL, url);
    }

    @Test
    void getLogoUrl_baseIndisponible_doitRenvoyerLeLogoParDefaut() {
        // Préparation : la base de données plante
        when(parametreRepository.findLogoMeta("app.logo")).thenThrow(new RuntimeException("BDD KO"));

        // Exécution
        String url = parametreService.getLogoUrl();

        // Vérification
        assertEquals(ParametreService.DEFAULT_LOGO_URL, url);
    }

    @Test
    void getLogoUrl_typeDeFichierVide_doitRenvoyerLeLogoParDefaut() {
        // Préparation : un logo existe mais son type est vide (valeur invalide)
        ParametreRepository.LogoMeta meta = mock(ParametreRepository.LogoMeta.class);
        when(meta.getContentType()).thenReturn("");
        when(parametreRepository.findLogoMeta("app.logo")).thenReturn(meta);

        // Exécution
        String url = parametreService.getLogoUrl();

        // Vérification
        assertEquals(ParametreService.DEFAULT_LOGO_URL, url);
    }

    @Test
    void getLogoUrl_avecLogo_doitRenvoyerLUrlDuLogo() {
        // Préparation : un vrai logo en base
        ParametreRepository.LogoMeta meta = mock(ParametreRepository.LogoMeta.class);
        when(meta.getContentType()).thenReturn("image/png");
        when(meta.getValeurParametre()).thenReturn("logo_abc.png");
        when(parametreRepository.findLogoMeta("app.logo")).thenReturn(meta);

        // Exécution
        String url = parametreService.getLogoUrl();

        // Vérification
        assertEquals("/parametres/logo?v=logo_abc.png", url);
    }

    @Test
    void hasCustomLogo_sansLogo_doitEtreFaux() {
        assertFalse(parametreService.hasCustomLogo());
    }

    @Test
    void getLogoData_sansParametre_doitRenvoyerNull() {
        assertNull(parametreService.getLogoData());
    }
}
