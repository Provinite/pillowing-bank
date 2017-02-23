package com.clovercoin.pillowing.repository;

import com.clovercoin.pillowing.entity.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByRole(String role);
}
