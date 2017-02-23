package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Transaction;

public interface TransactionService {
    Transaction saveTransaction(Transaction transaction);
}
