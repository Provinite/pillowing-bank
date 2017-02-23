package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.User;

public interface UserService {
    User findUserByEmail(String email);
    User createUser(User user);
    void addAdminRole(User user);
}
