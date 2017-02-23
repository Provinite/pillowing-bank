package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Role;
import com.clovercoin.pillowing.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        Assert.notNull(roleRepository, "This implementation of RoleService requires a RoleRepository.");

        this.roleRepository = roleRepository;
    }

    @Override
    public void createRoleIfNotExists(Role role) {
        if (roleRepository.findByRole(role.getRole()) == null) {
            roleRepository.save(role);
        }
    }
}
