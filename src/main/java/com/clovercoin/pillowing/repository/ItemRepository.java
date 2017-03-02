package com.clovercoin.pillowing.repository;

import com.clovercoin.pillowing.constant.ItemType;
import com.clovercoin.pillowing.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {
    Page<Item> findByItemType(ItemType itemType, Pageable pageable);
    Page<Item> findByNameStartsWithAndItemType(String name, ItemType itemType, Pageable pageable);
    Page<Item> findByNameContainsAndItemType(String name, ItemType itemType, Pageable pageable);

    Item findByName(String name);
}
