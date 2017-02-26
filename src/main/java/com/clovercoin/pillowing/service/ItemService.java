package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Item;
import org.springframework.data.domain.Page;

public interface ItemService {
    Item saveItem(Item item);
    Page<Item> getPage(Integer page);
}
