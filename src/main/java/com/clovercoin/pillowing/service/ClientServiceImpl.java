package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import com.clovercoin.pillowing.repository.ClientRepository;
import com.clovercoin.pillowing.repository.InventoryLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {
    private Sort defaultSort;
    private Integer defaultPageSize;

    private Sort defaultInventoryLineSort;

    private ClientRepository clientRepository;
    private InventoryLineRepository inventoryLineRepository;

    @Autowired
    private ClientServiceImpl(ClientRepository clientRepository, InventoryLineRepository inventoryLineRepository) {
        Assert.notNull(clientRepository, "This implementation of ClientService requires a ClientRepository.");
        Assert.notNull(inventoryLineRepository, "This implementation of ClientService requires an InventoryLineRepository.");

        this.clientRepository = clientRepository;
        this.inventoryLineRepository = inventoryLineRepository;

        this.defaultSort = new Sort(Sort.Direction.ASC, "name");
        this.defaultInventoryLineSort = new Sort(Sort.Direction.ASC, "item.name");
        this.defaultPageSize = 20;
    }

    @Override
    public Client getById(Long id) {
        return clientRepository.findOne(id);
    }

    @Override
    public Client saveClient(Client client) {
        Client result;
        try {
            result = clientRepository.save(client);
        } catch (DataIntegrityViolationException dive) {
            throw new DuplicateKeyException("A client with the given name already exists", dive);
        }
        return result;
    }

    @Override
    public List<InventoryLine> getInventory(Client client) {
        return inventoryLineRepository.findByClient(client);
    }

    @Override
    public Page<InventoryLine> getInventoryPage(Client client, Integer page) {
        return inventoryLineRepository.findByClient(client, new PageRequest(page, defaultPageSize, defaultInventoryLineSort));
    }

    @Override
    public Page<Client> getPage(Integer page) {
        Pageable pageable = new PageRequest(page, defaultPageSize, defaultSort);
        return clientRepository.findAll(pageable);
    }

    @Override
    public InventoryLine saveInventoryLine(InventoryLine inventoryLine) {
        return inventoryLineRepository.save(inventoryLine);
    }
}
