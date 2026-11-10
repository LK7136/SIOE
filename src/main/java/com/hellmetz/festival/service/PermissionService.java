package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Permission;
import com.hellmetz.festival.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Permission> findAllById(Iterable<Long> ids) {
        return permissionRepository.findAllById(ids);
    }

}
