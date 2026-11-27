package com.hellmetz.festival.controller;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Vérifie que les pages d'erreur existent bien là où Spring Boot les cherche
 * (templates/error/CODE.html). Si une page est supprimée ou renommée, le test échoue.
 */
class PagesErreurTest {

    @Test
    void pageErreur403_doitExister() {
        assertTrue(new ClassPathResource("templates/error/403.html").exists());
    }

    @Test
    void pageErreur404_doitExister() {
        assertTrue(new ClassPathResource("templates/error/404.html").exists());
    }

    @Test
    void pageErreur405_doitExister() {
        assertTrue(new ClassPathResource("templates/error/405.html").exists());
    }

    @Test
    void pageErreur500_doitExister() {
        assertTrue(new ClassPathResource("templates/error/500.html").exists());
    }
}
