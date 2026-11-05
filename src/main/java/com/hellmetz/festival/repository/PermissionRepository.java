package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    //vide car spring fait automatiqement findall
}
