package com.clovercoin.pillowing.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(length = 2048)
    private String note;

    @Column(unique = true)
    private String normalizedName;

    @PrePersist @PreUpdate private void prepare(){
        this.normalizedName = (name == null) ? null : name.toLowerCase();
    }
}
