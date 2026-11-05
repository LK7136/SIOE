package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Parametre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametreRepository extends JpaRepository<Parametre, Long> {
    Parametre findByCodeParametre(String codeParametre);
}
