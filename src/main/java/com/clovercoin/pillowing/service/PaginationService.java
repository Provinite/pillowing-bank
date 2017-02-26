package com.clovercoin.pillowing.service;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public interface PaginationService {
    List<PaginationElement> getPaginationElements(Page<?> page, int maxElements);
}
