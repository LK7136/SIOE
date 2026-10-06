package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Artiste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// JpaRepository<T, ID> : T = votre entité, ID = type de sa clé primaire
public interface ArtisteRepository extends JpaRepository<Artiste, Long> {

    // chercher les artistes par nom de scene
    List<Artiste> findAllByOrderByNomSceneAsc();

    // chercher les artistes par groupe
    List<Artiste> findByGroupeId(Long idGroupe);

}
