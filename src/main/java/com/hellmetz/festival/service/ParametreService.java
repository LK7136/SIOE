package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.repository.ParametreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class ParametreService {

    private static final Logger log = LoggerFactory.getLogger(ParametreService.class);

    private final ParametreRepository parametreRepository;

    public ParametreService(ParametreRepository parametreRepository) {this.parametreRepository = parametreRepository;}

    @Transactional(readOnly = true)
    public List<Parametre> findAll() {
        log.debug("Recherche de tous les paramètres");
        return parametreRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Parametre findByCode(String code) {
        Parametre parametre = parametreRepository.findByCodeParametre(code);
        if (parametre == null) {
            log.warn("Paramètre '{}' introuvable", code);
        }
        return parametre;
    }

    @Transactional(readOnly = true)
    public Parametre findById(Long id) {
        log.debug("Recherche du paramètre {}", id);
        return parametreRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Paramètre {} introuvable", id);
                    return new RuntimeException("Parametre introuvable : " + id);
                });
    }

    public Parametre save(Parametre parametre) {
        Parametre saved = parametreRepository.save(parametre);
        log.info("Paramètre {} enregistré", saved.getId());
        return saved;
    }

}