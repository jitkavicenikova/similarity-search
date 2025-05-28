package thesis.domain.search.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Marker;
import thesis.data.model.Result;
import thesis.data.model.Technology;
import thesis.data.service.MarkerService;
import thesis.data.service.query.ResultBoolQueryService;
import thesis.domain.search.dto.BoolSearchOptions;
import thesis.domain.search.dto.SearchFilters;
import thesis.domain.search.service.helpers.TechnologyResolver;
import thesis.domain.search.validation.BoolSearchValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BoolSearchServiceTest {
    @Mock
    private ResultBoolQueryService resultBoolQueryService;

    @Mock
    private MarkerService markerService;

    @Mock
    private TechnologyResolver technologyResolver;

    @Mock
    private BoolSearchValidator validator;

    @InjectMocks
    private BoolSearchService boolSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processBoolSearch_ShouldReturnResults_WhenValidTechnologyAndMarkerProvided() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("markerX");
        var filters = new SearchFilters("techA", null, null, null, null);
        options.setFilters(filters);
        options.setValue(true);

        Marker marker = new Marker("markerX", "desc", null, null);
        Technology tech = new Technology("techA");

        Result expectedResult = new Result(1, "markerX", null, null,
                null, null, true, "sample", "techA", null, null, "unitRaw", LocalDateTime.now());

        when(markerService.getEntity("markerX")).thenReturn(marker);
        when(technologyResolver.resolveTechnologyNames("markerX", filters)).thenReturn(Set.of("techA"));
        when(resultBoolQueryService.getAllBoolResults("markerX", true, Set.of("techA")))
                .thenReturn(List.of(expectedResult));

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertEquals(1, results.size());
        assertEquals("markerX", results.get(0).getMarkerName());
        verify(validator).validateOptions(options);
    }

    @Test
    void processBoolSearch_ShouldReturnResultsFilteredBySpecificity_WhenMinSpecificityIsProvided() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("markerX");
        options.setMinSpecificity(0.8);
        options.setValue(false);

        Marker marker = new Marker("markerX", "desc", null, null);
        Result expectedResult = new Result(2, "markerX", null, null, null, null, false, "sample", null, null, null, "unitRaw", LocalDateTime.now());

        when(markerService.getEntity("markerX")).thenReturn(marker);
        when(technologyResolver.resolveTechnologyNamesByThresholds("markerX", null, 0.8))
                .thenReturn(Set.of("tech1", "tech2"));
        when(resultBoolQueryService.getAllBoolResults("markerX", false, Set.of("tech1", "tech2")))
                .thenReturn(List.of(expectedResult));

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertEquals(1, results.size());
        assertEquals(false, results.get(0).getBooleanValue());
    }

    @Test
    void processBoolSearch_ShouldIntersectSpecificityAndSensitivity_WhenBothThresholdsProvided() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("markerX");
        options.setMinSpecificity(0.8);
        options.setMinSensitivity(0.7);
        options.setValue(true);

        Marker marker = new Marker("markerX", "desc", null, null);
        Result expectedResult = new Result(3, "markerX", null, null,
                null, null, true, "sample", "tech2", null, null, "unitRaw", LocalDateTime.now());

        when(markerService.getEntity("markerX")).thenReturn(marker);
        when(technologyResolver.resolveTechnologyNamesByThresholds("markerX", 0.7, 0.8))
                .thenReturn(Set.of("tech2"));
        when(resultBoolQueryService.getAllBoolResults("markerX", true, Set.of("tech2")))
                .thenReturn(List.of(expectedResult));

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertEquals(1, results.size());
        assertEquals("markerX", results.get(0).getMarkerName());
    }

    @Test
    void processBoolSearch_ShouldHandleEmptyTechnologySet_WhenNoThresholdsMet() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("markerX");
        options.setMinSpecificity(0.9);  // High threshold that no technology meets
        options.setValue(true);

        Marker marker = new Marker("markerX", "desc", null, null);

        when(markerService.getEntity("markerX")).thenReturn(marker);
        when(technologyResolver.resolveTechnologyNamesByThresholds("markerX", null, 0.9))
                .thenReturn(Set.of());

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertEquals(0, results.size());
    }

    @Test
    void processBoolSearch_ShouldThrowException_WhenOptionsAreInvalid() {
        BoolSearchOptions options = new BoolSearchOptions();

        doThrow(new IllegalArgumentException("Invalid options")).when(validator).validateOptions(options);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            boolSearchService.processBoolSearch(options);
        });
        assertEquals("Invalid options", exception.getMessage());
        verify(validator).validateOptions(options);
    }
}
