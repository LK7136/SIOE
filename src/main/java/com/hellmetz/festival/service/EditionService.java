package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Edition;
import com.hellmetz.festival.repository.EditionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class EditionService {

    private static final Logger log = LoggerFactory.getLogger(EditionService.class);

    private final EditionRepository editionRepository;

    public EditionService(EditionRepository editionRepository) {
        this.editionRepository = editionRepository;
    }

    @Transactional(readOnly = true)
    public List<Edition> findAll() {
        log.debug("Recherche de toutes les éditions");
        return editionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Edition findById(Long id) {
        log.debug("Recherche de l'édition {}", id);
        Edition edition = editionRepository.findById(id).orElse(null);
        if (edition == null) {
            log.warn("Édition {} introuvable", id);
        }
        return edition;
    }
}