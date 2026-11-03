package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Style;
import com.hellmetz.festival.repository.StyleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class StyleService {

    private static final Logger log = LoggerFactory.getLogger(StyleService.class);

    private final StyleRepository styleRepository;

    public StyleService(StyleRepository styleRepository) {
        this.styleRepository = styleRepository;
    }

    @Transactional(readOnly = true)
    public List<Style> findAll() {
        log.debug("Recherche de tous les styles");
        return styleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Style findById(Long id) {
        log.debug("Recherche du style {}", id);
        Style style = styleRepository.findById(id).orElse(null);
        if (style == null) {
            log.warn("Style {} introuvable", id);
        }
        return style;
    }
}