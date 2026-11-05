package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Groupe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// JpaRepository<T, ID> : T = votre entité, ID = type de sa clé primaire
public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    List<Groupe> findAllByOrderByNomGroupeAsc();

    @Query("SELECT g FROM Groupe g WHERE g.id NOT IN (SELECT c.groupe.id FROM Concert c WHERE c.groupe IS NOT NULL) ORDER BY g.nomGroupe ASC")
    List<Groupe> findGroupesSansConcert();

    @Query("SELECT g FROM Groupe g WHERE g.id NOT IN (SELECT c.groupe.id FROM Concert c WHERE c.groupe IS NOT NULL) ORDER BY g.nomGroupe ASC")
    Page<Groupe> findGroupesSansConcertPage(Pageable pageable);
}


