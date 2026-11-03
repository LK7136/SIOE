package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.repository.GroupeRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class GroupeService {

    private static final Logger log = LoggerFactory.getLogger(GroupeService.class);

    private final GroupeRepository groupeRepository;
    private static final Sort TRI = Sort.by("nomGroupe").ascending();

    public GroupeService(GroupeRepository groupeRepository) {
        this.groupeRepository = groupeRepository;
    }

    @Transactional(readOnly = true)
    public Page<Groupe> findPage(int page, int taille) {
        log.debug("Pagination des groupes : page={}, taille={}", page, taille);
        return groupeRepository.findAll(PageRequest.of(page, taille, TRI));
    }

    @Transactional(readOnly = true)
    public Page<Groupe> findTout() {
        log.debug("Récupération de tous les groupes sans pagination");
        return groupeRepository.findAll(Pageable.unpaged(TRI));
    }

    @Transactional(readOnly = true)
    public List<Groupe> findAll() {
        log.debug("Recherche de tous les groupes");
        return groupeRepository.findAllByOrderByNomGroupeAsc();
    }

    @Transactional(readOnly = true)
    public Groupe findById(Long id) {
        log.debug("Recherche du groupe {}", id);
        return groupeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Groupe {} introuvable", id);
                    return new RuntimeException("Groupe introuvable : " + id);
                });
    }

    @Transactional
    public Groupe findByIdWithStyles(Long id) {
        log.debug("Recherche du groupe {} avec ses styles", id);
        Groupe groupe = groupeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Groupe {} introuvable", id);
                    return new RuntimeException("Groupe introuvable : " + id);
                });

        if (groupe.getStylesDuGroupe() != null) {
            Hibernate.initialize(groupe.getStylesDuGroupe());
        }

        return groupe;
    }

    public Groupe save(Groupe groupe) {
        Groupe saved = groupeRepository.save(groupe);
        log.info("Groupe {} enregistré", saved.getId());
        return saved;
    }

    public void deleteById(Long id) {
        groupeRepository.deleteById(id);
        log.info("Groupe {} supprimé", id);
    }

    @Transactional(readOnly = true)
    public List<Groupe> findGroupesSansConcert() {
        log.debug("Recherche des groupes sans concert");
        return groupeRepository.findGroupesSansConcert();
    }

    @Transactional(readOnly = true)
    public Page<Groupe> findGroupesSansConcertPage(int page, int taille) {
        log.debug("Pagination des groupes sans concert : page={}, taille={}", page, taille);
        return groupeRepository.findGroupesSansConcertPage(org.springframework.data.domain.PageRequest.of(page, taille));
    }

    @Transactional(readOnly = true)
    public Page<Groupe> findGroupesSansConcertTout() {
        log.debug("Récupération de tous les groupes sans concert");
        List<Groupe> liste = groupeRepository.findGroupesSansConcert();
        return new org.springframework.data.domain.PageImpl<>(liste, org.springframework.data.domain.Pageable.unpaged(), liste.size());
    }
}