package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Item;
import com.clovercoin.pillowing.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        Assert.notNull(itemRepository, "This implementation of ItemService requires an ItemRepository.");

        this.itemRepository = itemRepository;
    }

    @Override
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }
}
