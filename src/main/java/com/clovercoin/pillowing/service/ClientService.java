package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;

import java.util.List;

public interface ClientService {
    Client saveClient(Client client);
    List<InventoryLine> getInventory(Client client);
}
