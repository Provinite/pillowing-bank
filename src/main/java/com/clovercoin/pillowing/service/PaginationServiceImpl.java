package com.clovercoin.pillowing.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("paginationService")
public class PaginationServiceImpl implements PaginationService {
    public List<PaginationElement> getPaginationElements(Page<?> page, int maxElements) {
        List<PaginationElement> result = new ArrayList<>();
        int maxNumDynamicElements = maxElements - 2;
        int maxDynamicIndex = page.getTotalPages()-2;
        int minDynamicIndex = 1;

        if (maxElements >= page.getTotalPages()) {
            for (int i = 0; i < page.getTotalPages(); i++) {
                PaginationElement pe = new PaginationElement(i, false, i == page.getNumber());
                result.add(pe);
            }
            return result;
        }

        int balancedUp = (maxNumDynamicElements - 1) / 2;
        int balancedDown = balancedUp;

        System.out.println(balancedUp + " - " + balancedDown);

        if (balancedUp + balancedDown < maxNumDynamicElements-1) {
            balancedUp += 1;
        }

        System.out.println(balancedUp + " - " + balancedDown);

        int balancedStart = page.getNumber() - balancedDown;
        int balancedEnd = page.getNumber() + balancedUp;

        int start = Math.max(balancedStart, minDynamicIndex);
        int end = Math.min(balancedEnd, maxDynamicIndex);

        if (start > balancedStart) {
            for (int i = 0; i < start - balancedStart; i++) {
                end++;
            }
        } else if (end < balancedEnd) {
            for (int i = 0; i < balancedEnd - end; i++) {
                start--;
            }
        }

        PaginationElement ellipsis = new PaginationElement(-1, true, false);
        PaginationElement first = new PaginationElement(0, false, page.isFirst());
        PaginationElement last = new PaginationElement(page.getTotalPages() - 1, false, page.isLast());
        result.add(first);
        if (start > minDynamicIndex) {
            result.add(ellipsis);
        }
        for (int i = start; i <= end; i++) {
            PaginationElement pe = new PaginationElement(i, false, i == page.getNumber());
            result.add(pe);
        }
        if (end < maxDynamicIndex) {
            result.add(ellipsis);
        }
        result.add(last);

        return result;
    }
}
