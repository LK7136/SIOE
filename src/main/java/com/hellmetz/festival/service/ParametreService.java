package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.repository.ParametreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ParametreService {

    private final ParametreRepository parametreRepository;

    public ParametreService(ParametreRepository parametreRepository) {this.parametreRepository = parametreRepository;}

    @Transactional(readOnly = true)
    public List<Parametre> findAll() {return parametreRepository.findAll();}

    @Transactional(readOnly = true)
    public Parametre findByCode(String code) {return parametreRepository.findByCodeParametre(code);}

    @Transactional(readOnly = true)
    public Parametre findById(Long id) {return parametreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Parametre introuvable : " + id));
    }

    public Parametre save(Parametre parametre) {return parametreRepository.save(parametre);}

}