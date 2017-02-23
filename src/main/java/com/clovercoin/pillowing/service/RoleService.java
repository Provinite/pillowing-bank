package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Role;

public interface RoleService {
    void createRoleIfNotExists(Role role);
}
