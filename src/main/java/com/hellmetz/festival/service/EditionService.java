package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Artiste;
import com.hellmetz.festival.model.Edition;
import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.repository.EditionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EditionService {

    private final EditionRepository editionRepository;

    public EditionService(EditionRepository editionRepository) {
        this.editionRepository = editionRepository;
    }

    @Transactional(readOnly = true)
    public List<Edition> findAll() {
        return editionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Edition findById(Long id) {
        return editionRepository.findById(id).orElse(null);
    }

    // doit afficher uniquement les editions actifs
    public Optional<Edition> findActive() {
        return editionRepository.findByActifTrue();
    }

    // doit rendre une edition actif
    @Transactional
    public Edition activer(Long id) {
        Edition edition = editionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Edition introuvable"));

        editionRepository.findByActifTrue().ifPresent(ancienne -> {
            ancienne.setActif(false);
            editionRepository.save(ancienne);
        });

        edition.setActif(true);
        return editionRepository.save(edition);
    }

    public Edition save(Edition edition) {
        return editionRepository.save(edition);
    }

    public void deleteById(Long id) {
        editionRepository.deleteById(id);
    }

    public void delete(Edition edition) {
        editionRepository.delete(edition);
    }

    public void valider(Edition edition) {
        if (edition.getDateDebut() == null || edition.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires.");
        }
        if (edition.getDateFin().isBefore(edition.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début.");
        }
        int annee = edition.getAnnee();
        if (edition.getDateDebut().getYear() != annee || edition.getDateFin().getYear() != annee) {
            throw new IllegalArgumentException(
                    "Les dates doivent être comprises entre le 01/01/" + annee + " et le 31/12/" + annee + ".");
        }
    }

}
