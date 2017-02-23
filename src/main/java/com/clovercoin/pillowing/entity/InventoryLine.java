package com.clovercoin.pillowing.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client","item"})
        })
@Data
public class InventoryLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "client")
    private Client user;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "item")
    private Item item;

    @Column
    private Integer quantity;
}
