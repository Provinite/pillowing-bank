package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClientService {
    Client saveClient(Client client);
    List<InventoryLine> getInventory(Client client);
    Page<Client> getPage(Integer page);
}
