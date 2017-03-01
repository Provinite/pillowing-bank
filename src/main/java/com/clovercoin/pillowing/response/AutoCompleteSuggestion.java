package com.clovercoin.pillowing.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AutoCompleteSuggestion {
    String value;
    Map<String, Object> data;

    public AutoCompleteSuggestion() {
        this.value = "";
        this.data = new HashMap<>();
    }

    public AutoCompleteSuggestion(String value) {
        this.value = value;
        this.data = new HashMap<>();
    }
}
