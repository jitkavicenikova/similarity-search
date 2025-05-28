package thesis.data.service.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import thesis.data.model.Result;
import thesis.data.repository.ResultRepository;
import thesis.domain.search.dto.RecursiveResult;
import thesis.domain.search.dto.enums.NumericSearchType;
import thesis.exceptions.EntityNotFoundException;
import thesis.utils.EntityUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ResultNumericQueryServiceTest {
    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private ResultNumericQueryService resultNumericQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getResultsByIds_ShouldReturnResults_WhenIdsExist() {
        String markerName = "marker1";
        Set<String> recordIds = Set.of("1", "2");
        Result result1 = new Result();
        Result result2 = new Result();
        when(resultRepository.findById(EntityUtils.generateResultId("1", markerName))).thenReturn(Optional.of(result1));
        when(resultRepository.findById(EntityUtils.generateResultId("2", markerName))).thenReturn(Optional.of(result2));

        List<Result> results = resultNumericQueryService.getResultsByIds(recordIds, markerName);

        assertEquals(Set.of(result1, result2), new HashSet<>(results));
    }

    @Test
    void getResultsByIds_ShouldThrowException_WhenIdNotFound() {
        String markerName = "marker1";
        Set<String> recordIds = Set.of("1");
        when(resultRepository.findById(EntityUtils.generateResultId("1", markerName)))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> resultNumericQueryService.getResultsByIds(recordIds, markerName));
    }

    @Test
    void getAllResultsForMarker_ShouldCombineMinAndMaxResults() {
        String markerName = "marker4";
        boolean useTechnologyDeviations = true;

        Set<ZSetOperations.TypedTuple<String>> minResults = Set.of(new DefaultTypedTuple<>("result1", 10.0));
        Set<ZSetOperations.TypedTuple<String>> maxResults = Set.of(new DefaultTypedTuple<>("result1", 20.0));

        when(resultRepository.getAllMinResultsForMarker(markerName, useTechnologyDeviations)).thenReturn(minResults);
        when(resultRepository.getAllMaxResultsForMarker(markerName, useTechnologyDeviations)).thenReturn(maxResults);

        List<RecursiveResult> results = resultNumericQueryService.getAllResultsForMarker(markerName, useTechnologyDeviations);

        assertEquals(1, results.size());
        assertEquals("result1", results.get(0).getRecordId());
        assertEquals(10.0, results.get(0).getMin());
        assertEquals(20.0, results.get(0).getMax());
    }

    @Test
    void searchResults_ShouldReturnResultsForMaximumMatches_WhenMinIsNull_AndSearchTypeIsExactMatch() {
        String markerName = "marker1";
        Double max = 100.0;
        boolean useTechnologyDeviations = true;
        Set<String> expectedResults = Set.of("result1", "result2");

        when(resultRepository.searchForMaximumMatches(markerName, Double.NEGATIVE_INFINITY, max, useTechnologyDeviations))
                .thenReturn(expectedResults);

        Set<String> results = resultNumericQueryService.searchResults(markerName, null, max, NumericSearchType.EXACT_MATCH, useTechnologyDeviations);

        assertEquals(expectedResults, results);
    }

    @Test
    void searchResults_ShouldReturnResultsForMinimumMatches_WhenMaxIsNull_AndSearchTypeIsExactMatch() {
        String markerName = "marker2";
        Double min = 50.0;
        boolean useTechnologyDeviations = false;
        Set<String> expectedResults = Set.of("result3");

        when(resultRepository.searchForMinimumMatches(markerName, min, Double.POSITIVE_INFINITY, useTechnologyDeviations))
                .thenReturn(expectedResults);

        Set<String> results = resultNumericQueryService.searchResults(markerName, min, null, NumericSearchType.EXACT_MATCH, useTechnologyDeviations);

        assertEquals(expectedResults, results);
    }
}

