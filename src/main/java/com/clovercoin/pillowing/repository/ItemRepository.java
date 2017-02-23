package com.clovercoin.pillowing.repository;

import com.clovercoin.pillowing.entity.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Long> {
}
