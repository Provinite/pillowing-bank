package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClientService {
    Client getById(Long id);
    Client saveClient(Client client);
    List<InventoryLine> getInventory(Client client);
    Page<InventoryLine> getInventoryPage(Client client, Integer page);
    Page<InventoryLine> getCurrencyPage(Client client, Integer page);
    Page<Client> getPage(Integer page);
    InventoryLine saveInventoryLine(InventoryLine line);
}
