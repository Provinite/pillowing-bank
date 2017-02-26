package com.clovercoin.pillowing.repository;

import com.clovercoin.pillowing.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}
