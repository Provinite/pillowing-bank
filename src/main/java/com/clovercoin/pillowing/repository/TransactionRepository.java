package com.clovercoin.pillowing.repository;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.Item;
import com.clovercoin.pillowing.entity.Transaction;
import com.clovercoin.pillowing.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
    List<Transaction> findByClient(Client client);
    Page<Transaction> findByClient(Client client, Pageable pageable);
    Page<Transaction> findByClientAndItem(Client client, Item item, Pageable pageable);
    Page<Transaction> findByUser(User user, Pageable pageable);
}
