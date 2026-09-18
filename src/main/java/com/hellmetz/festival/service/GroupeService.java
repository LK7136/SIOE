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

@Service
@Transactional
public class GroupeService {

    private final GroupeRepository groupeRepository;
    private static final Sort TRI = Sort.by("nomGroupe").ascending();

    public GroupeService(GroupeRepository groupeRepository) {
        this.groupeRepository = groupeRepository;
    }

    @Transactional(readOnly = true)
    public Page<Groupe> findPage(int page, int taille) {
        return groupeRepository.findAll(PageRequest.of(page, taille, TRI));
    }

    @Transactional(readOnly = true)
    public Page<Groupe> findTout() {
        return groupeRepository.findAll(Pageable.unpaged(TRI));
    }

    @Transactional(readOnly = true)
    public List<Groupe> findAll() {
        return groupeRepository.findAllByOrderByNomGroupeAsc();
    }

    @Transactional(readOnly = true)
    public Groupe findById(Long id) {
        return groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable : " + id));
    }

    @Transactional
    public Groupe findByIdWithStyles(Long id) {
        Groupe groupe = groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable : " + id));

        if (groupe.getStylesDuGroupe() != null) {
            Hibernate.initialize(groupe.getStylesDuGroupe());
        }

        return groupe;
    }

    public Groupe save(Groupe groupe) {
        return groupeRepository.save(groupe);
    }

    public void deleteById(Long id) {
        groupeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Groupe> findGroupesSansConcert() {
        return groupeRepository.findGroupesSansConcert();
    }

    @Transactional(readOnly = true)
    public Page<Groupe> findGroupesSansConcertPage(int page, int taille) {
        return groupeRepository.findGroupesSansConcertPage(org.springframework.data.domain.PageRequest.of(page, taille));
    }

    @Transactional(readOnly = true)
    public Page<Groupe> findGroupesSansConcertTout() {
        List<Groupe> liste = groupeRepository.findGroupesSansConcert();
        return new org.springframework.data.domain.PageImpl<>(liste, org.springframework.data.domain.Pageable.unpaged(), liste.size());
    }
}