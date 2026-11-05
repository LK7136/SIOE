package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Scene;
import com.hellmetz.festival.repository.SceneRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SceneService {

    private final SceneRepository sceneRepository;

    public SceneService(SceneRepository sceneRepository) {
        this.sceneRepository = sceneRepository;
    }

    @Transactional(readOnly = true)
    public List<Scene> findAll() {
        return sceneRepository.findAllByOrderByNomAsc();
    }

    @Transactional(readOnly = true)
    public Page<Scene> findPage(int page, int taille) {
        return sceneRepository.findAll(PageRequest.of(page, taille, Sort.by("nom").ascending()));
    }

    @Transactional(readOnly = true)
    public Page<Scene> findTout() {
        return sceneRepository.findAll(Pageable.unpaged(Sort.by("nom").ascending()));
    }

    @Transactional(readOnly = true)
    public Scene findById(Long id) {
        return sceneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scene introuvable : " + id));
    }

    public void save(Scene scene) {
        sceneRepository.save(scene);
    }

    public void deleteById(Long id) {
        sceneRepository.deleteById(id);
    }
}
