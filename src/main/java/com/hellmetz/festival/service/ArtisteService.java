package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Artiste;
import com.hellmetz.festival.repository.ArtisteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArtisteService {

    @Autowired
    private ArtisteRepository artisteRepository;

    public List<Artiste> findAll() {
        return artisteRepository.findAllByOrderByNomSceneAsc();
    }

    public Artiste findById(Long id) {return artisteRepository.findById(id).orElseThrow(() -> new RuntimeException("Artiste introuvable"));}

    public List<Artiste> findByGroupeId(Long idGroupe) {
        return artisteRepository.findByGroupeId(idGroupe);
    }

    public void save(Artiste artiste) {
        artisteRepository.save(artiste);
    }

    public void deleteById(Long id) {
        artisteRepository.deleteById(id);
    }

    public void delete(Artiste artiste) {
        artisteRepository.delete(artiste);
    }

    @Transactional(readOnly = true)
    public Page<Artiste> findPage(int page, int taille) {
        return artisteRepository.findAll(PageRequest.of(page, taille, Sort.by("nomScene").ascending()));
    }

    @Transactional(readOnly = true)
    public Page<Artiste> findTout() {
        List<Artiste> tousLesArtistes = findAll();
        return new PageImpl<>(tousLesArtistes);
    }

    public Page<Artiste> findDisponiblesOuGroupe(Long groupeId, int page, int taille) {
        return artisteRepository.findByGroupeIdOrGroupeIsNull(groupeId, PageRequest.of(page, taille));
    }

    public Page<Artiste> findDisponiblesOuGroupeTout(Long groupeId) {
        return artisteRepository.findByGroupeIdOrGroupeIsNull(groupeId, Pageable.unpaged());
    }

    public Page<Artiste> findDisponibles(int page, int taille) {
        return artisteRepository.findByGroupeIsNull(PageRequest.of(page, taille));
    }

    public Page<Artiste> findDisponiblesTout() {
        return artisteRepository.findByGroupeIsNull(Pageable.unpaged());
    }
}
