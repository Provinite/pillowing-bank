package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Transaction;
import org.springframework.data.domain.Page;

public interface TransactionService {
    Transaction saveTransaction(Transaction transaction);
    Page<Transaction> getPage(Integer page);
}
