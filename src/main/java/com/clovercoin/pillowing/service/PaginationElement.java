package com.clovercoin.pillowing.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class PaginationElement {
    private int index;
    private boolean ellipsis;
    private boolean active;

    public PaginationElement(int index, boolean ellipsis, boolean active) {
        this.index = index;
        this.ellipsis = ellipsis;
        this.active = active;
    }
}
