package com.clovercoin.pillowing;

import com.clovercoin.pillowing.service.PaginationElement;
import com.clovercoin.pillowing.service.PaginationService;
import com.clovercoin.pillowing.service.PaginationServiceImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaginationServiceImplTests {
    @Mock
    private Page<?> page;

    private PaginationServiceImpl paginationService;

    @Before
    public void setup() {
        page = mock(Page.class);
        paginationService = new PaginationServiceImpl();
    }

    @Test
    public void GetPaginationElements_Should_ReturnAllPagesWithNoEllipses_When_MaxElementsGreaterThanTotalPages() {
        when(page.getTotalPages()).thenReturn(3);
        when(page.getNumber()).thenReturn(1);
        when(page.isFirst()).thenReturn(true);
        when(page.isLast()).thenReturn(false);

        List<PaginationElement> result = paginationService.getPaginationElements(page, 4);
        assertThat(result).hasSize(3);
        assertThat(result.stream().map(PaginationElement::isEllipsis).anyMatch(b -> b))
                .withFailMessage("The resulting pagination list should contain no ellipses").isFalse();
    }

    @Test
    public void GetPaginationElements_Should_ReturnMaxElementsPagesIncludingFirstAndLastAndOneLeadingEllipsis_When_MaxElementsDoesNotSpanTotalPagesAndCurrentPageIsNearEnd() {
        int numPages = 40;
        int curPage = 37;
        int maxElements = 10;

        int expectedStart = 31;
        int expectedEnd = 38;

        mockPage(page, numPages, curPage, false, false);

        List<PaginationElement> result = paginationService.getPaginationElements(page, maxElements);
        assertThat(result).hasSize(maxElements + 1); //maxElements + 1 ellipsis
        assertThat(result.get(1).isEllipsis())
                .withFailMessage("The second element should be an ellipsis").isEqualTo(true);
        testFullList(page, result, maxElements, 2, expectedStart, expectedEnd);
    }

    @Test
    public void GetPaginationElements_Should_ReturnMaxElementsPagesIncludingFirstAndLastAndOneTrailingEllipsis_When_MaxElementsDoesNotSpanTotalPagesAndCurrentPageIsNearStart() {
        int numPages = 40;
        int curPage = 3;
        int maxElements = 9;

        int expectedStart = 1;
        int expectedEnd = 7;

        mockPage(page, numPages, curPage, false, false);

        List<PaginationElement> result = paginationService.getPaginationElements(page, maxElements);
        assertThat(result).hasSize(maxElements + 1); //maxElements + 1 ellipsis
        assertThat(result.get(result.size() - 2).isEllipsis())
                .withFailMessage("The second to last element should be an ellipsis").isEqualTo(true);

        testFullList(page, result, maxElements, 1, expectedStart, expectedEnd);
    }

    @Test
    public void GetPaginationElements_Should_ReturnMaxElementsPagesIncludingFirstAndLastAndOneLeadingAndTrailingEllipsis_When_MaxElementsDoesNotSpanTotalPagesAndCurrentPageIsNearMid() {
        int numPages = 40;
        int curPage = 20;
        int maxElements = 10;

        int expectedStart = 17;
        int expectedEnd = 24;

        mockPage(page, numPages, curPage, false, false);

        List<PaginationElement> result = paginationService.getPaginationElements(page, maxElements);
        assertThat(result).hasSize(maxElements + 2); //maxElements + 2 ellipses
        assertThat(result.get(result.size() - 2).isEllipsis())
                .withFailMessage("The second to last element should be an ellipsis").isEqualTo(true);
        assertThat(result.get(1).isEllipsis())
                .withFailMessage("The second element should be an ellipsis").isEqualTo(true);

        testFullList(page, result, maxElements, 2, expectedStart, expectedEnd);
    }

    private void mockPage(Page<?> page, int numPages, int curPage, boolean first, boolean last) {
        when(page.getTotalPages()).thenReturn(numPages);
        when(page.getNumber()).thenReturn(curPage);
        when(page.isFirst()).thenReturn(false);
        when(page.isLast()).thenReturn(false);
    }

    private void testFullList(Page<?> page, List<PaginationElement> result, int maxElements, int dynamicStart, int expectedStart, int expectedEnd) {
        assertThat(result.get(0).getIndex())
                .withFailMessage("The first element should point at the first page.").isEqualTo(0);
        assertThat(result.get(result.size() - 1).getIndex())
                .withFailMessage("The last element should point at the last page").isEqualTo(page.getTotalPages() - 1);
        for (int i = 0; i < maxElements - 2; i++) {
            assertThat(result.get(i+dynamicStart).getIndex())
                    .withFailMessage("The dynamic portion of the list should run from "
                            + expectedStart
                            + " through "
                            + expectedEnd
                            + ". Element [" + i + "] of the dynamic portion should point at [" + i+expectedStart + "] "
                            + "Instead: [" + result.get(i+dynamicStart).getIndex() + "]"
                            + ".").isEqualTo(i + expectedStart);
        }
    }
}
