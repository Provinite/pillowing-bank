package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Transaction;
import com.clovercoin.pillowing.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class TransactionServiceImpl implements TransactionService {
    private TransactionRepository transactionRepository;
    private Sort defaultSort;
    private Integer defaultSize;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        Assert.notNull(transactionRepository, "This implementation of TransactionService requires a TransactionRepository");

        this.transactionRepository = transactionRepository;

        defaultSort = new Sort(Sort.Direction.DESC, "timestamp");
        defaultSize = 20;
    }

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Page<Transaction> getPage(Integer page) {
        Pageable pageable = new PageRequest(page, defaultSize, defaultSort);
        return transactionRepository.findAll(pageable);
    }
}
