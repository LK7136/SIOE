package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Concert;
import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.repository.ConcertRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class ConcertService {

    private final ConcertRepository concertRepository;
    private static final Sort TRI_CONCERT = Sort.by("id").ascending();

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    @Transactional(readOnly = true)
    public Page<Concert> findPage(int page, int taille) {
        return concertRepository.findAll(PageRequest.of(page, taille, TRI_CONCERT));
    }

    @Transactional(readOnly = true)
    public Page<Concert> findTout() {
        return concertRepository.findAll(Pageable.unpaged(TRI_CONCERT));
    }

    @Transactional(readOnly = true)
    public List<Concert> findAll() {
        return concertRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Concert findById(Long id) {
        return concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concert introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Concert> findByGroupeId(Long groupeId) {
        return concertRepository.findByGroupeId(groupeId);
    }


    @Transactional(readOnly = true)
    public Page<Concert> findDisponiblesOuAnnulesOuGroupe(Long groupeId, int page, int taille) {
        return concertRepository.findDisponiblesOuAnnulesOuGroupe(groupeId, PageRequest.of(page, taille, TRI_CONCERT));
    }

    @Transactional(readOnly = true)
    public Page<Concert> findDisponiblesOuAnnulesOuGroupeTout(Long groupeId) {
        return concertRepository.findDisponiblesOuAnnulesOuGroupe(groupeId, Pageable.unpaged(TRI_CONCERT));
    }

    @Transactional(readOnly = true)
    public Page<Concert> findDisponiblesOuAnnules(int page, int taille) {
        return concertRepository.findDisponiblesOuAnnules(PageRequest.of(page, taille, TRI_CONCERT));
    }

    @Transactional(readOnly = true)
    public Page<Concert> findDisponiblesOuAnnulesTout() {
        return concertRepository.findDisponiblesOuAnnules(Pageable.unpaged(TRI_CONCERT));
    }

    // sauvegarde des concerts cochés pour un groupe
    public void updateGroupeConcerts(Long groupeId, List<Long> concertIds, List<Long> visibleConcertIds, Groupe groupe) {
        for (Long visibleId : visibleConcertIds) {
            concertRepository.findById(visibleId).ifPresent(c -> {
                if (concertIds.contains(visibleId)) {
                    c.setGroupe(groupe);
                } else if (c.getGroupe() != null && c.getGroupe().getId().equals(groupeId)) {
                    c.setGroupe(null);
                }
                concertRepository.save(c);
            });
        }
    }

    public void save(Concert concert) {
        concertRepository.save(concert);
    }

    public void deleteById(Long id) {
        concertRepository.deleteById(id);
    }


}
