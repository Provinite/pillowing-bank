package com.clovercoin.pillowing.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("constantService")
public class ConstantServiceImpl implements ConstantService {
    private Map<String, Object> constants;

    public ConstantServiceImpl() {
        constants = new ConcurrentHashMap<>();
        constants.put("MODEL_KEY_STATUS", "MK_STATUS");
        constants.put("MODEL_KEY_ACTION", "MK_ACTION");
        constants.put("MODEL_KEY_MESSAGE", "MK_MESSAGE");
        constants.put("MODEL_KEY_ERROR", "MK_ERROR");

        constants.put("ACTION_NONE", 0);
        constants.put("ACTION_ADD", 1);
        constants.put("ACTION_ADD_SUBMIT", 2);
        constants.put("ACTION_EDIT", 3);
        constants.put("ACTION_EDIT_SUBMIT", 4);
        constants.put("ACTION_DELETE", 5);
        constants.put("ACTION_DELETE_SUBMIT", 6);

        constants.put("STATUS_NONE", 0);
        constants.put("STATUS_SUCCESS", 1);
        constants.put("STATUS_FAILURE", 2);

        constants.put("ERROR_UNKNOWN", 0);
        constants.put("ERROR_DUPLICATE_KEY", 1);

        constants.put("ROLE_ADMIN", "ADMIN");
        constants.put("ROLE_MOD", "MOD");
        constants.put("ROLE_GUEST_ARTIST", "GUEST_ARTIST");
        constants.put("ROLE_CLIENT", "CLIENT");
    }

    @Override
    public Object get(String key) {
        if (!constants.containsKey(key)) {
            throw new IllegalArgumentException("No constant with the name " + key + " is defined.");
        }
        return constants.get(key);
    }

    @Override
    public String getString(String key) {
        return String.valueOf(this.get(key));
    }

    @Override
    public Map<String, Object> getAllWithPrefix(String prefix) {
        Map<String, Object> result = new HashMap<>();
        constants.entrySet().stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .forEach(e -> result.put(e.getKey(), e.getValue()));
        if (result.isEmpty()) {
            throw new IllegalArgumentException("No constants with prefix " + prefix + " are defined.");
        }
        return result;
    }
}
