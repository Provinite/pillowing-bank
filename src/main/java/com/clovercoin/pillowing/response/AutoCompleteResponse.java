package com.clovercoin.pillowing.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AutoCompleteResponse {
    List<AutoCompleteSuggestion> suggestions;

    public AutoCompleteResponse() {
        this.suggestions = new ArrayList<>();
    }
}
