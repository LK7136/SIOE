package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Concert;
import com.hellmetz.festival.model.Edition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {
    // tous les concerts d'une journée donnée
    List<Concert> findByDateHeureDebutBetween(LocalDateTime debut, LocalDateTime fin);

    // concerts d'une édition (pour la progression)
    List<Concert> findByEdition(Edition edition);

    List<Concert> findAllByOrderByIdAsc();

    List<Concert> findByGroupeId(Long groupeId);

    // trouver des concerts sans groupe ou annulés ou déjà assignés à ce groupe
    @Query("SELECT c FROM Concert c WHERE (c.groupe IS NULL AND (c.statut = 'Non programmé' OR c.statut = 'Annulé')) " +
            "OR (c.groupe.id = :groupeId) " +
            "ORDER BY CASE WHEN c.groupe.id = :groupeId THEN 0 ELSE 1 END ASC, c.dateHeureDebut ASC")
    Page<Concert> findDisponiblesOuAnnulesOuGroupe(@Param("groupeId") Long groupeId, Pageable pageable);

    // troyver un concert qui est anullé ou qui na pas de groupe
    @Query("SELECT c FROM Concert c WHERE c.groupe IS NULL OR c.statut = 'Annulé'")
    Page<Concert> findDisponiblesOuAnnules(Pageable pageable);
}
