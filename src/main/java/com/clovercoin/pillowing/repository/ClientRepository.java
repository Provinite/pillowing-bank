package com.clovercoin.pillowing.repository;

import com.clovercoin.pillowing.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClientRepository extends PagingAndSortingRepository<Client, Long> {
    Page<Client> findByNameStartsWith(String name, Pageable pageable);
    Client findByName(String name);
}
