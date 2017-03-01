package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.constant.ItemType;
import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import com.clovercoin.pillowing.entity.Item;
import com.clovercoin.pillowing.entity.Transaction;
import com.clovercoin.pillowing.repository.ClientRepository;
import com.clovercoin.pillowing.repository.InventoryLineRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {
    private Sort defaultSort;
    private Integer defaultPageSize;

    private Sort defaultInventoryLineSort;

    private ClientRepository clientRepository;
    private InventoryLineRepository inventoryLineRepository;

    private UserService userService;
    private TransactionService transactionService;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository,
                              InventoryLineRepository inventoryLineRepository,
                              UserService userService,
                              TransactionService transactionService) {
        Assert.notNull(clientRepository, "This implementation of ClientService requires a ClientRepository.");
        Assert.notNull(inventoryLineRepository, "This implementation of ClientService requires an InventoryLineRepository.");
        Assert.notNull(userService, "This implementation of ClientService requires a UserService.");
        Assert.notNull(transactionService, "This implementation of ClientService requires a TransactionService.");

        this.clientRepository = clientRepository;
        this.inventoryLineRepository = inventoryLineRepository;
        this.userService = userService;
        this.transactionService = transactionService;

        this.defaultSort = new Sort(Sort.Direction.ASC, "name");
        this.defaultInventoryLineSort = new Sort(Sort.Direction.ASC, "item.name");
        this.defaultPageSize = 20;
    }

    @Override
    public Client getById(Long id) {
        return clientRepository.findOne(id);
    }

    @Override
    public Client saveClient(Client client) {
        Client result;
        try {
            result = clientRepository.save(client);
        } catch (DataIntegrityViolationException dive) {
            if (dive.getCause() instanceof ConstraintViolationException)
                throw new DuplicateKeyException("A client with the given name already exists", dive);
            else
                throw dive;
        }
        return result;
    }

    @Override
    public List<InventoryLine> getInventory(Client client) {
        return inventoryLineRepository.findByClient(client);
    }

    @Override
    public Page<InventoryLine> getInventoryPage(Client client, Integer page) {
        return inventoryLineRepository.findByClientAndItemItemType(
                client,
                ItemType.ITEM,
                new PageRequest(page, defaultPageSize, defaultInventoryLineSort));
    }

    @Override
    public Page<InventoryLine> getCurrencyPage(Client client, Integer page) {
        return inventoryLineRepository.findByClientAndItemItemType(
                client,
                ItemType.CURRENCY,
                new PageRequest(page, defaultPageSize, defaultInventoryLineSort));
    }

    @Override
    public Page<Client> getPage(Integer page) {
        Pageable pageable = new PageRequest(page, defaultPageSize, defaultSort);
        return clientRepository.findAll(pageable);
    }

    @Override
    public InventoryLine saveInventoryLine(InventoryLine inventoryLine) {
        return inventoryLineRepository.save(inventoryLine);
    }

    @Override
    public InventoryLine getInventoryLine(Client client, Item item) {
        return inventoryLineRepository.findByClientAndItem(client, item);
    }

    @Override
    @Transactional
    public Transaction updateInventoryQuantity(Client client, Item item, Integer quantity, String note) {
        InventoryLine line = inventoryLineRepository.findByClientAndItem(client, item);
        boolean lineExists;
        if (line == null) {
            lineExists = false;
            line = new InventoryLine();
            line.setClient(client);
            line.setItem(item);
            line.setQuantity(0);
        } else {
            lineExists = true;
            if (quantity.equals(line.getQuantity())) {
                //no change to make
                return null;
            }
        }

        //non-currency items should not have 0-lines created for them
        //nothing to do
        if (!lineExists && quantity.equals(0) && item.getItemType() == ItemType.ITEM) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setClient(client);
        transaction.setItem(item);
        transaction.setUser(userService.getCurrentUser());
        transaction.setQuantityChange(quantity - line.getQuantity());
        transaction.setTimestamp(new Date());
        transaction.setNote(note);

        transaction = transactionService.saveTransaction(transaction);

        if (lineExists && quantity.equals(0) && item.getItemType() == ItemType.ITEM) {
            //remove the line instead of setting quantity to 0
            inventoryLineRepository.delete(line);
        } else {
            line.setQuantity(quantity);
            inventoryLineRepository.save(line);
        }

        return transaction;
    }
}
