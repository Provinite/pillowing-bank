package com.clovercoin.pillowing.entity;

import com.clovercoin.pillowing.constant.ItemType;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    /*
    @Column
    @Enumerated(EnumType.STRING)
    private ItemType type;*/
}
