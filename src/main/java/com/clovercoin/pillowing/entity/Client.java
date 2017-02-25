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
}
