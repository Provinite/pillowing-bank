package com.clovercoin.pillowing.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ConstantService {
    Object get(String key);
    String getString(String key);
    Map<String, Object> getAllWithPrefix(String prefix);
}
