package com.clovercoin.pillowing.repository;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByClient(Client client);
}
