package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Artiste;
import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.repository.ArtisteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class ArtisteService {

    private static final Logger log = LoggerFactory.getLogger(ArtisteService.class);

    @Autowired
    private ArtisteRepository artisteRepository;

    public List<Artiste> findAll() {
        log.debug("Recherche de tous les artistes");
        return artisteRepository.findAllByOrderByNomSceneAsc();
    }

    public Artiste findById(Long id) {
        log.debug("Recherche de l'artiste {}", id);
        return artisteRepository.findById(id).orElseThrow(() -> {
            log.warn("Artiste {} introuvable", id);
            return new RuntimeException("Artiste introuvable");
        });
    }

    public List<Artiste> findByGroupeId(Long idGroupe) {
        log.debug("Recherche des artistes du groupe {}", idGroupe);
        return artisteRepository.findByGroupeId(idGroupe);
    }

    public void save(Artiste artiste) {
        artisteRepository.save(artiste);
        log.info("Artiste {} enregistré", artiste.getId());
    }

    public void deleteById(Long id) {
        artisteRepository.deleteById(id);
        log.info("Artiste {} supprimé", id);
    }

    public void delete(Artiste artiste) {
        artisteRepository.delete(artiste);
        log.info("Artiste {} supprimé", artiste.getId());
    }

    @Transactional(readOnly = true)
    public Page<Artiste> findPage(int page, int taille) {
        log.debug("Pagination des artistes : page={}, taille={}", page, taille);
        return artisteRepository.findAll(PageRequest.of(page, taille, Sort.by("nomScene").ascending()));
    }

    @Transactional(readOnly = true)
    public Page<Artiste> findTout() {
        log.debug("Récupération de tous les artistes sans pagination");
        List<Artiste> tousLesArtistes = findAll();
        return new PageImpl<>(tousLesArtistes);
    }
}