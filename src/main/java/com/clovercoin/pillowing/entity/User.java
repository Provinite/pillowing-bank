package com.clovercoin.pillowing.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="APP_USER")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password;
}
