package com.clovercoin.pillowing.repository;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InventoryLineRepository extends CrudRepository<InventoryLine, Long> {
    List<InventoryLine> findByClient(Client client);
}
