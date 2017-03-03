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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @Column(unique = true)
    private String normalizedName;

    @PrePersist @PreUpdate private void prepare(){
        this.normalizedName = (name == null) ? null : name.toLowerCase();
    }
}
