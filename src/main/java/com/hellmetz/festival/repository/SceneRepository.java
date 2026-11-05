package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SceneRepository extends JpaRepository<Scene, Long> {
    List<Scene> findAllByOrderByNomAsc();
}
