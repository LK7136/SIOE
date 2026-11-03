package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Role;
import com.hellmetz.festival.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<Role> findAll() {
        log.debug("Recherche de tous les rôles");
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role findById(Long id) {
        log.debug("Recherche du rôle {}", id);
        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) {
            log.warn("Rôle {} introuvable", id);
        }
        return role;
    }

    // ajouter pour recupere les roles avec perm
    @Transactional(readOnly = true)
    public List<Role> findAllAvecPermissions() {
        log.debug("Recherche de tous les rôles avec leurs permissions");
        List<Role> roles = roleRepository.findAll();
        roles.forEach(r -> r.getPermissions().size());
        return roles;
    }

    public Role save(Role role) {
        Role saved = roleRepository.save(role);
        log.info("Rôle {} enregistré", saved.getIdRole());
        return saved;
    }

}