package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Parametre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParametreRepository extends JpaRepository<Parametre, Long> {
    Parametre findByCodeParametre(String codeParametre);

    interface LogoMeta {
        String getContentType();
        String getValeurParametre();
    }

    @Query("select p.contentType as contentType, p.valeurParametre as valeurParametre "
            + "from Parametre p where p.codeParametre = :code")
    LogoMeta findLogoMeta(@Param("code") String code);
}
