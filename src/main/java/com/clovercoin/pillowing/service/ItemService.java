package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Item;
import org.springframework.data.domain.Page;

public interface ItemService {
    Item saveItem(Item item);
    Item getByName(String name);
    Page<Item> getPage(Integer page);
    Page<Item> getItemPage(Integer page);
    Page<Item> getCurrencyPage(Integer page);
    Page<Item> searchItemsByName(String name, Integer page);
    Page<Item> searchCurrenciesByName(String name, Integer page);
    Item getById(Long id);
}
