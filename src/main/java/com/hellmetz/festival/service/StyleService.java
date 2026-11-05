package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Style;
import com.hellmetz.festival.repository.StyleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StyleService {

    private final StyleRepository styleRepository;

    public StyleService(StyleRepository styleRepository) {
        this.styleRepository = styleRepository;
    }

    @Transactional(readOnly = true)
    public List<Style> findAll() {
        return styleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Style findById(Long id) {
        return styleRepository.findById(id).orElse(null);
    }
}
