package com.hellmetz.festival.repository;

import com.hellmetz.festival.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// JpaRepository<T, ID> : T = votre entité, ID = type de sa clé primaire
public interface RoleRepository extends JpaRepository<Role, Long> {
}

