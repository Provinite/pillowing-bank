package com.clovercoin.pillowing.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name="APP_USER")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(length = 512)
    private String password;

    @Column
    private int active;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name="user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Column(unique = true)
    private String normalizedUsername;

    @Column(unique = true)
    private String normalizedEmail;

    @PrePersist @PreUpdate private void prepare() {
        this.normalizedUsername = (username == null) ? null : username.toLowerCase();
        this.normalizedEmail = (email == null) ? null : email.toLowerCase();
    }
}
