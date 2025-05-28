package thesis.domain.search.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Result;
import thesis.domain.search.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class AdvancedSearchServiceTest {
    @InjectMocks
    private AdvancedSearchService advancedSearchService;

    @Mock
    private NumericSearchService numericSearchService;

    @Mock
    private StringSearchService stringSearchService;

    @Mock
    private BoolSearchService boolSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processAdvancedSearch_ShouldReturnIntersectionOfRecordIds() {
        AdvancedSearchOptions options = new AdvancedSearchOptions();

        BoolSearchOptions boolOption = new BoolSearchOptions();
        options.setBoolOptions(List.of(boolOption));
        Result boolResult1 = new Result(1, "markerX", null, null, null, null, true, "sample", null, null, null, null, LocalDateTime.now());
        boolResult1.setRecordId("1");
        Result boolResult2 = new Result(2, "markerX", null, null, null, null, true, "sample", null, null, null, null, LocalDateTime.now());
        boolResult2.setRecordId("2");
        when(boolSearchService.processBoolSearch(boolOption)).thenReturn(List.of(boolResult1, boolResult2));

        NumericSearchOptions numericOption = new NumericSearchOptions();
        options.setNumericOptions(List.of(numericOption));
        NumericSearchResult numericSearchResult = new NumericSearchResult(null, null);
        Result numericResult1 = new Result(2, "markerX", 3.0, 8.0, null, null, null, "sample", null, null, null, null, LocalDateTime.now());
        numericResult1.setRecordId("2");
        Result numericResult2 = new Result(3, "markerX", 4.0, 7.0, null, null, null, "sample", null, null, null, null, LocalDateTime.now());
        numericResult2.setRecordId("3");
        numericSearchResult.setResults(List.of(numericResult1, numericResult2));
        when(numericSearchService.processNumericSearch(numericOption)).thenReturn(numericSearchResult);

        StringSearchOptions stringOption = new StringSearchOptions();
        options.setStringOptions(List.of(stringOption));
        Result stringResult = new Result(2, "markerX", null, null, "true", "cat", null, "sample", null, null, null, null, LocalDateTime.now());
        stringResult.setRecordId("2");
        when(stringSearchService.processStringSearch(stringOption)).thenReturn(List.of(stringResult));

        Set<String> result = advancedSearchService.processAdvancedSearch(options);

        assertEquals(Set.of("2"), result);
    }

    @Test
    void processAdvancedSearch_ShouldReturnEmptySet_WhenAllOptionsAreEmpty() {
        AdvancedSearchOptions options = new AdvancedSearchOptions();
        Set<String> result = advancedSearchService.processAdvancedSearch(options);
        assertEquals(Set.of(), result);
    }

    @Test
    void processAdvancedSearch_ShouldHandleNullResultsForNumericSearch() {
        AdvancedSearchOptions options = new AdvancedSearchOptions();
        NumericSearchOptions numericOption = new NumericSearchOptions();
        options.setNumericOptions(List.of(numericOption));

        NumericSearchResult numericSearchResult = new NumericSearchResult(null, null);
        numericSearchResult.setAggregatedResults(List.of(new AggregatedResult("100", null, null, null, null)));
        when(numericSearchService.processNumericSearch(numericOption)).thenReturn(numericSearchResult);

        Set<String> result = advancedSearchService.processAdvancedSearch(options);
        assertEquals(Set.of("100"), result);
    }

    @Test
    void processAdvancedSearch_ShouldReturnEmptySet_WhenAllOptionListsAreNull() {
        AdvancedSearchOptions options = new AdvancedSearchOptions();
        Set<String> result = advancedSearchService.processAdvancedSearch(options);
        assertEquals(Set.of(), result);
    }
}
