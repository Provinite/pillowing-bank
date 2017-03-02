package com.clovercoin.pillowing.forms;

import lombok.Data;

@Data
public class InventoryModificationForm {
    private Long itemId;
    private Long clientId;
    private Integer quantity;
    private String itemName;
    private String note;
}
