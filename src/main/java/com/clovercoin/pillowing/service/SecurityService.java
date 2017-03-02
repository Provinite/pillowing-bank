package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.entity.User;

public interface SecurityService {
    Integer expireUserSessions(User user);
}
