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

    @Transactional(readOnly = true)
    public List<Role> findAllAvecPermissions() {return roleRepository.findAllAvecPermissions();}

    @Transactional(readOnly = true)
    public Role findByIdAvecPermissions(Long id) { return roleRepository.findByIdAvecPermissions(id).orElse(null); }

    public Role save(Role role) { return roleRepository.save(role);}

}
