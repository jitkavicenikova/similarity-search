package thesis.domain.search.service.helpers;

import org.junit.jupiter.api.Test;
import thesis.data.model.Result;
import thesis.domain.search.dto.SearchFilters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultFilterUtilTest {
    private final Result result1 = new Result(1, "Ca", null, null, null, null, null, "blood", "techA", null, null, null, LocalDateTime.of(2023, 1, 1, 10, 0));
    private final Result result2 = new Result(2, "Fe", null, null, null, null, null, "plasma", "techB", null, null, null, LocalDateTime.of(2023, 1, 2, 10, 0));
    private final Result result3 = new Result(3, "Na", null, null, null, null, null, "blood", "techA", null, null, null, LocalDateTime.of(2023, 1, 3, 10, 0));

    @Test
    void filterResults_ShouldReturnAll_WhenFiltersIsNull() {
        List<Result> results = List.of(result1, result2);

        List<Result> filtered = ResultFilterUtil.filterResults(results, null);

        assertEquals(results, filtered);
    }

    @Test
    void filterResults_ShouldFilterBySample() {
        SearchFilters filters = new SearchFilters();
        filters.setSample("blood");

        List<Result> filtered = ResultFilterUtil.filterResults(List.of(result1, result2, result3), filters);

        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(result1));
        assertTrue(filtered.contains(result3));
    }

    @Test
    void filterResults_ShouldFilterByFromTimestamp() {
        SearchFilters filters = new SearchFilters();
        filters.setFromTimestamp(LocalDateTime.of(2023, 1, 2, 0, 0));

        List<Result> filtered = ResultFilterUtil.filterResults(List.of(result1, result2, result3), filters);

        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(result2));
        assertTrue(filtered.contains(result3));
    }

    @Test
    void filterResults_ShouldFilterByToTimestamp() {
        SearchFilters filters = new SearchFilters();
        filters.setToTimestamp(LocalDateTime.of(2023, 1, 2, 0, 0));

        List<Result> filtered = ResultFilterUtil.filterResults(List.of(result1, result2, result3), filters);

        assertEquals(1, filtered.size());
        assertTrue(filtered.contains(result1));
    }

    @Test
    void filterResults_ShouldFilterByTechnologyName() {
        List<Result> results = List.of(result1, result2, result3);
        SearchFilters filters = new SearchFilters();

        List<Result> filtered = ResultFilterUtil.filterResults(results, filters, Set.of("techA"));

        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(result1));
        assertTrue(filtered.contains(result3));
    }

    @Test
    void filterResults_ShouldReturnEmpty_WhenTechnologyDoesNotMatch() {
        List<Result> results = List.of(result1, result2);
        SearchFilters filters = new SearchFilters();

        List<Result> filtered = ResultFilterUtil.filterResults(results, filters, Set.of("techX"));

        assertEquals(0, filtered.size());
    }

    @Test
    void filterResults_ShouldApplyAllFiltersTogether() {
        SearchFilters filters = new SearchFilters();
        filters.setSample("blood");
        filters.setFromTimestamp(LocalDateTime.of(2023, 1, 2, 0, 0));
        filters.setToTimestamp(LocalDateTime.of(2023, 1, 4, 0, 0));

        List<Result> filtered = ResultFilterUtil.filterResults(List.of(result1, result2, result3), filters, Set.of("techA"));

        assertEquals(1, filtered.size());
        assertEquals(result3, filtered.get(0));
    }

    @Test
    void filterResults_ShouldReturnEmpty_WhenInputListIsEmpty() {
        SearchFilters filters = new SearchFilters();
        filters.setSample("blood");

        List<Result> filtered = ResultFilterUtil.filterResults(emptyList(), filters);

        assertEquals(0, filtered.size());
    }
}
