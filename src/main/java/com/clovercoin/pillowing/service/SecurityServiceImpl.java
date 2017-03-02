package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

@Service
public class SecurityServiceImpl implements SecurityService {
    private SessionRegistry sessionRegistry;

    @Autowired
    public SecurityServiceImpl(SessionRegistry sessionRegistry) {
        Assert.notNull(sessionRegistry, "This implementation of SecurityService requires a SessionRegistry");
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public Integer expireUserSessions(User user) {
        int result = 0;
        Set<SessionInformation> sessionsToDestroy = new HashSet<>();
        sessionRegistry.getAllPrincipals().forEach(principal -> {
            if (principal instanceof UserDetails) {
                UserDetails current = (UserDetails) principal;
                if (current.getUsername().equals(user.getEmail())) {
                    sessionsToDestroy.addAll(sessionRegistry.getAllSessions(current, false));
                }
            }
        });
        for (SessionInformation session : sessionsToDestroy) {
            result++;
            session.expireNow();
        }

        return result;
    }
}
