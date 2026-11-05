package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

        List<Utilisateur> findByIdentifiantAndActifTrue(String identifiant);

        Optional<Utilisateur> findByIdentifiant(String identifiant);

        @Query("select count(u) from Utilisateur u join u.roles r where r.codeRole = 'ADMIN'")
        long countAdmins();
}
