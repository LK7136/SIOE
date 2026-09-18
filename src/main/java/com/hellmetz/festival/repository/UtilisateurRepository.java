package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
// JpaRepository<T, ID> : T = votre entité, ID = type de sa clé primaire
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

        // cherche les utilisateur par leur identifaint et si l'utilisateur est actif
        List<Utilisateur> findByIdentifiantAndActifTrue(String identifiant);

        Optional<Utilisateur> findByIdentifiant(String identifiant);

}
