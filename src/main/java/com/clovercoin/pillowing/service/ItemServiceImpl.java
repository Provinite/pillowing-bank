package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.Item;
import com.clovercoin.pillowing.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private Integer defaultPageSize;
    private Sort defaultSort;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        Assert.notNull(itemRepository, "This implementation of ItemService requires an ItemRepository.");

        this.itemRepository = itemRepository;

        this.defaultSort = new Sort(Sort.Direction.ASC, "name");
        this.defaultPageSize = 20;
    }

    @Override
    public Item saveItem(Item item) {
        try {
            return itemRepository.save(item);
        } catch (DataIntegrityViolationException dive) {
            throw new DuplicateKeyException("An item with the given name already exists");
        }
    }

    @Override
    public Page<Item> getPage(Integer page) {
        Pageable pageable = new PageRequest(page, defaultPageSize, defaultSort);
        return itemRepository.findAll(pageable);
    }

    @Override
    public Item getById(Long id) {
        return itemRepository.findOne(id);
    }
}
