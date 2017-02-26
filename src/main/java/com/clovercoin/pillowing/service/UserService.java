package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.User;
import com.clovercoin.pillowing.forms.UserAddForm;

import java.util.List;

public interface UserService {
    User findUserByEmail(String email);
    User createUser(User user);
    User addRole(User user, String role);
    User saveUser(User user, boolean changedPassword);
    User createUserFromForm(UserAddForm form);
    List<User> getAllUsers();
    Boolean userHasRole(User user, String role);
}
