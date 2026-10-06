package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Edition;
import com.hellmetz.festival.repository.EditionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
