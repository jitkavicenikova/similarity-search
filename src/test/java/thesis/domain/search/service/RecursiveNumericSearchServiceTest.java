package thesis.domain.search.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.enums.AggregationType;
import thesis.data.model.Marker;
import thesis.data.model.Unit;
import thesis.data.service.MarkerService;
import thesis.data.service.query.ResultNumericQueryService;
import thesis.domain.search.dto.AggregatedResult;
import thesis.domain.search.dto.NumericSearchConfiguration;
import thesis.domain.search.dto.RecursiveResult;
import thesis.domain.search.dto.enums.NumericSearchType;
import thesis.domain.search.service.helpers.SearchConversionService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RecursiveNumericSearchServiceTest {
    @Mock
    private SearchConversionService searchConversionService;
    @Mock
    private ResultNumericQueryService resultService;
    @Mock
    private MarkerService markerService;

    @InjectMocks
    private RecursiveNumericSearchService recursiveNumericSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getResultsForMarkerWithChildren_ShouldReturnAggregatedResults2() {
        Marker parentMarker = createMarker("parentMarker", List.of("child1", "child2"), AggregationType.SUM);
        Unit unit = new Unit();
        NumericSearchConfiguration config = new NumericSearchConfiguration();
        config.setSearchType(NumericSearchType.EXACT_MATCH);
        config.setWithTolerance(false);
        config.setMinimum(2.0);
        config.setMaximum(10.0);

        RecursiveResult childResult1 = new RecursiveResult("rec1", 2.0, 4.0);
        RecursiveResult childResult2 = new RecursiveResult("rec1", 3.0, 5.0);

        when(markerService.getEntity("child1")).thenReturn(createMarker("child1", null, AggregationType.SUM));
        when(markerService.getEntity("child2")).thenReturn(createMarker("child2", null, AggregationType.SUM));

        when(resultService.getAllResultsForMarker("child1", null)).thenReturn(List.of(childResult1));
        when(resultService.getAllResultsForMarker("child2", null)).thenReturn(List.of(childResult2));

        when(searchConversionService.convertRecursiveResults(any(), any(), any())).thenAnswer(invocation -> {
            Marker m = invocation.getArgument(0);
            if ("child1".equals(m.getName())) {
                return List.of(childResult1);
            } else if ("child2".equals(m.getName())) {
                return List.of(childResult2);
            }
            return List.of();
        });

        List<AggregatedResult> aggregatedResults =
                recursiveNumericSearchService.getResultsForMarkerWithChildren(config, parentMarker, unit);

        assertThat(aggregatedResults).hasSize(1);
        AggregatedResult result = aggregatedResults.get(0);
        assertEquals("rec1", result.getRecordId());
        assertEquals(5.0, result.getMin());
        assertEquals(9.0, result.getMax());
    }

    @Test
    void getResultsForMarkerWithChildren_ShouldReturnEmptyList_WhenNoResultsFound() {
        Marker marker = createMarker("marker", null, AggregationType.SUM);
        Unit unit = new Unit();
        NumericSearchConfiguration config = new NumericSearchConfiguration();

        when(resultService.getAllResultsForMarker(any(), anyBoolean())).thenReturn(Collections.emptyList());

        List<AggregatedResult> results = recursiveNumericSearchService.getResultsForMarkerWithChildren(config, marker, unit);

        assertThat(results).isEmpty(); // No results aggregated when no recursive results exist
    }

    private Marker createMarker(String name, List<String> childNames, AggregationType aggregationType) {
        Marker marker = new Marker();
        marker.setName(name);
        marker.setChildMarkerNames(childNames);
        marker.setAggregationType(aggregationType);
        return marker;
    }
}
