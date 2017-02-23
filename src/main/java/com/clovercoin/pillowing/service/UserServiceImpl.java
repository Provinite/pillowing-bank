package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.SecurityConfiguration;
import com.clovercoin.pillowing.entity.Role;
import com.clovercoin.pillowing.entity.User;
import com.clovercoin.pillowing.repository.RoleRepository;
import com.clovercoin.pillowing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        Assert.notNull(userRepository, "This implementation of UserService requires a UserRepository.");
        Assert.notNull(roleRepository, "This implementation of UserService requires a RoleRepository");
        Assert.notNull(bCryptPasswordEncoder, "This implementation of UserService requires a BCryptPasswordEncoder");

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        return userRepository.save(user);
    }

    @Override
    public void addAdminRole(User user) {
        Role userRole = roleRepository.findByRole(SecurityConfiguration.ADMIN_ROLE);
        HashSet<Role> roles = new HashSet<>();
        if (user.getRoles() != null) {
            roles.addAll(user.getRoles());
        }
        roles.add(userRole);
        user.setRoles(roles);
    }
}
