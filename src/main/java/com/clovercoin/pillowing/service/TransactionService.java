package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.Transaction;
import com.clovercoin.pillowing.entity.User;
import org.springframework.data.domain.Page;

public interface TransactionService {
    Transaction saveTransaction(Transaction transaction);
    Page<Transaction> getPage(Integer page);

    Page<Transaction> searchByUser(User user, Integer page);
    Page<Transaction> searchByClient(Client client, Integer page);
}
