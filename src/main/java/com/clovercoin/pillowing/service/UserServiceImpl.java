package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.SecurityConfiguration;
import com.clovercoin.pillowing.entity.Role;
import com.clovercoin.pillowing.entity.User;
import com.clovercoin.pillowing.forms.UserAddForm;
import com.clovercoin.pillowing.repository.RoleRepository;
import com.clovercoin.pillowing.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

@Service("userService")
@Log
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
        Assert.notNull(user, "Cannot create null user.");
        user.setActive(1);
        return this.saveUser(user, true);
    }

    @Override
    public User addRole(User user, String role) {
        Assert.notNull(user, "Cannot add role: " + role + " to null user.");
        Assert.notNull(role, "Cannot add null role to user.");

        Role userRole = roleRepository.findByRole(role);
        if (userRole == null) {
            throw new NoSuchRoleException(role);
        }

        Set<Role> userRoles = user.getRoles();
        if (userRoles == null) {
            userRoles = new HashSet<>();
            user.setRoles(userRoles);
        }
        try {
            userRoles.add(userRole);
        } catch (UnsupportedOperationException ue) {
            Set<Role> newRoles = new HashSet<>();
            newRoles.addAll(userRoles);
            newRoles.add(userRole);
            user.setRoles(newRoles);
        }

        return user;
    }

    @Override
    public User saveUser(User user, boolean changedPassword) {
        Assert.notNull(user, "Cannot save null user.");
        if (changedPassword) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        try {
            user = userRepository.save(user);
            return user;
        } catch (DataIntegrityViolationException dive) {
            throw new DuplicateKeyException("A user with the given username or email already exists.", dive);
        }
    }

    @Override
    public User createUserFromForm(UserAddForm form) {
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        user.setUsername(form.getUsername());
        user.setActive(form.getIsActive() ? 1 : 0);

        if (form.getIsAdmin()) {
            this.addRole(user, SecurityConfiguration.ADMIN_ROLE);
        }
        if (form.getIsMod()) {
            this.addRole(user, SecurityConfiguration.MOD_ROLE);
        }
        if (form.getIsGuestArtist()) {
            this.addRole(user, SecurityConfiguration.GUEST_ARTIST_ROLE);
        }

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        userRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public Boolean userHasRole(User user, String role) {
        if (user.getRoles() == null || user.getRoles().isEmpty())
            return false;

        return user.getRoles().stream().map(Role::getRole).anyMatch(r -> r.equals(role));
    }

    @Override
    public User getCurrentUser() {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth == null
                || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                || currentAuth instanceof AnonymousAuthenticationToken)
            return null;
        UserDetails ud = (UserDetails)currentAuth.getPrincipal();
        String userEmail = ud.getUsername();
        return userRepository.findByEmail(userEmail);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findOne(id);
    }
}
