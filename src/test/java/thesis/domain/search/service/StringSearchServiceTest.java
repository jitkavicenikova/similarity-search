package thesis.domain.search.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Result;
import thesis.data.model.StringCategory;
import thesis.data.service.MarkerService;
import thesis.data.service.StringCategoryService;
import thesis.data.service.query.ResultStringQueryService;
import thesis.domain.search.dto.StringSearchOptions;
import thesis.domain.search.dto.enums.StringSearchType;
import thesis.domain.search.validation.StringSearchValidator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StringSearchServiceTest {
    @Mock
    private ResultStringQueryService resultService;

    @Mock
    private MarkerService markerService;

    @Mock
    private StringCategoryService stringCategoryService;

    @Mock
    private StringSearchValidator validator;

    @InjectMocks
    private StringSearchService stringSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenValueIsProvidedWithEQUALSearchType() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("markerX");
        options.setValue("value1");
        options.setSearchType(StringSearchType.EQUAL);

        Result expectedResult = new Result(1, "markerX", null, null,
                "value1", "cat", null, "sample", "techA", null, null, "unitRaw", LocalDateTime.now());

        when(resultService.getAllStringResultsForSingleValue("markerX", "value1", null))
                .thenReturn(List.of(expectedResult));

        List<Result> results = stringSearchService.processStringSearch(options);

        assertEquals(1, results.size());
        assertEquals("value1", results.get(0).getStringValue());
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenValueIsProvidedWithComparisonSearchType() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("markerX");
        options.setValue("value2");
        options.setSearchType(StringSearchType.GREATER_THAN);

        StringCategory category = new StringCategory("category1", true, List.of("value1", "value2", "value3"));

        when(stringCategoryService.getEntity("category1")).thenReturn(category);
        when(resultService.getAllStringResultsForMultipleValues(eq("markerX"), anyList(), eq(category)))
                .thenReturn(List.of(new Result(2, "markerX", null, null,
                        "value3", "cat", null, "sample", "techA", null, null, "unitRaw", LocalDateTime.now())));

        options.setCategoryName("category1");

        List<Result> results = stringSearchService.processStringSearch(options);

        assertEquals(1, results.size());
        assertEquals("value3", results.get(0).getStringValue());
    }

    @Test
    void processStringSearch_ShouldReturnAllResults_WhenNoValueOrValuesProvided() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("markerX");

        when(resultService.getAllStringResultsForCategory("markerX", null))
                .thenReturn(List.of(new Result(3, "markerX", null, null,
                        "value1", "cat", null, "sample", "techA", null, null, "unitRaw", LocalDateTime.now())));

        List<Result> results = stringSearchService.processStringSearch(options);

        assertEquals(1, results.size());
        assertEquals("value1", results.get(0).getStringValue());
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenMultipleValuesProvided() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("markerX");
        options.setValues(List.of("value1", "value3"));

        when(resultService.getAllStringResultsForMultipleValues("markerX", List.of("value1", "value3"), null))
                .thenReturn(Arrays.asList(
                        new Result(4, "markerX", null, null, "value1", "cat", null, "sample", "techA", null, null, "unitRaw", LocalDateTime.now()),
                        new Result(5, "markerX", null, null, "value3", "cat", null, "sample", "techA", null, null, "unitRaw", LocalDateTime.now())));

        List<Result> results = stringSearchService.processStringSearch(options);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(result -> result.getStringValue().equals("value1")));
        assertTrue(results.stream().anyMatch(result -> result.getStringValue().equals("value3")));
    }

    @Test
    void processStringSearch_ShouldThrowException_WhenOptionsAreInvalid() {
        StringSearchOptions options = new StringSearchOptions();

        doThrow(new IllegalArgumentException("Invalid options")).when(validator).validateOptions(options);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                stringSearchService.processStringSearch(options));

        assertEquals("Invalid options", exception.getMessage());
        verify(validator).validateOptions(options);
    }
}
