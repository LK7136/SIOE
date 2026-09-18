package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Role;
import com.hellmetz.festival.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    // ajouter pour recupere les roles avec perm
    @Transactional(readOnly = true)
    public List<Role> findAllAvecPermissions() {
        List<Role> roles = roleRepository.findAll();
        roles.forEach(r -> r.getPermissions().size());
        return roles;
    }

    public Role save(Role role) { return roleRepository.save(role);}



}
