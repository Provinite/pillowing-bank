package com.clovercoin.pillowing.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Role {
    public Role() {

    }

    public Role(String role) {
        this.setRole(role);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String role;
}
