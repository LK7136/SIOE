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

    // lit un parametre numerique, renvoie la valeur par defaut si absent ou invalide
    @Transactional(readOnly = true)
    public int getInt(String code, int defaut) {
        Parametre p = parametreRepository.findByCodeParametre(code);
        if (p == null || p.getValeurParametre() == null) {
            return defaut;
        }
        try {
            return Integer.parseInt(p.getValeurParametre().trim());
        } catch (NumberFormatException e) {
            return defaut;
        }
    }

    // taille de page par defaut (parametre "Elements par page"), sous forme de texte comme ?taille=
    @Transactional(readOnly = true)
    public String tailleParDefaut() {
        return String.valueOf(getInt("affichage.elements_par_page", 10));
    }

    // transforme ?taille=... en nombre, sinon taille par defaut
    @Transactional(readOnly = true)
    public int parseTaille(String taille) {
        try {
            int t = Integer.parseInt(taille);
            if (t > 0) {
                return t;
            }
        } catch (NumberFormatException e) {
            // on retombe sur la taille par defaut
        }
        int defaut = getInt("affichage.elements_par_page", 10);
        return defaut > 0 ? defaut : 10;
    }

    @Transactional(readOnly = true)
    public Parametre findById(Long id) {return parametreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Parametre introuvable : " + id));
    }

    public Parametre save(Parametre parametre) {return parametreRepository.save(parametre);}

}