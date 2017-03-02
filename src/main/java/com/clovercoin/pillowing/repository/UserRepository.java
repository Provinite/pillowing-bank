package com.clovercoin.pillowing.repository;

import com.clovercoin.pillowing.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    User findByEmail(String email);
    Page<User> findByUsernameContainsIgnoreCase(String username, Pageable pageable);
}
