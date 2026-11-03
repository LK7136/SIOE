package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Scene;
import com.hellmetz.festival.repository.SceneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class SceneService {

    private static final Logger log = LoggerFactory.getLogger(SceneService.class);

    private final SceneRepository sceneRepository;

    public SceneService(SceneRepository sceneRepository) {
        this.sceneRepository = sceneRepository;
    }

    @Transactional(readOnly = true)
    public List<Scene> findAll() {
        log.debug("Recherche de toutes les scènes");
        return sceneRepository.findAllByOrderByNomAsc();
    }

    @Transactional(readOnly = true)
    public Scene findById(Long id) {
        log.debug("Recherche de la scène {}", id);
        return sceneRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Scène {} introuvable", id);
                    return new RuntimeException("Scene introuvable : " + id);
                });
    }

    public void save(Scene scene) {
        sceneRepository.save(scene);
        log.info("Scène {} enregistrée", scene.getId());
    }

    public void deleteById(Long id) {
        sceneRepository.deleteById(id);
        log.info("Scène {} supprimée", id);
    }
}