package com.clovercoin.pillowing.service;

import org.springframework.data.domain.Page;
import java.util.List;

public interface PaginationService {
    List<PaginationElement> getPaginationElements(Page<?> page, int maxElements);
}
