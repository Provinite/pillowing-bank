package com.clovercoin.pillowing.repository;

import com.clovercoin.pillowing.constant.ItemType;
import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import com.clovercoin.pillowing.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface InventoryLineRepository extends PagingAndSortingRepository<InventoryLine, Long> {
    List<InventoryLine> findByClient(Client client);
    Page<InventoryLine> findByClient(Client client, Pageable pageable);

    InventoryLine findByClientAndItem(Client client, Item item);

    List<InventoryLine> findByClientAndItemItemType(Client client, ItemType type);
    Page<InventoryLine> findByClientAndItemItemType(Client client, ItemType type, Pageable pageable);

}
