package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Edition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EditionRepository extends JpaRepository<Edition, Long>{
    // l'édition courante = celle dont on a mis actif = true
    Optional<Edition> findByActifTrue();
}
