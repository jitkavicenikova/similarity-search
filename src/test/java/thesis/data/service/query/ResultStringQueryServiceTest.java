package thesis.data.service.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Result;
import thesis.data.model.StringCategory;
import thesis.data.repository.ResultRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ResultStringQueryServiceTest {
    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private ResultStringQueryService resultStringQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllStringResultsForSingleValue_ShouldReturnResults_WhenCategoryIsNotNull() {
        String markerName = "marker1";
        String value = "value1";
        StringCategory category = new StringCategory("category1", false, List.of("value1", "value2"));

        List<Result> expectedResults = List.of(new Result(), new Result());
        when(resultRepository.getAllByMarkerNameAndStringValueAndStringValueCategory(markerName, value, "category1"))
                .thenReturn(expectedResults);

        List<Result> actualResults = resultStringQueryService.getAllStringResultsForSingleValue(markerName, value, category);

        assertEquals(expectedResults, actualResults);
    }

    @Test
    void getAllStringResultsForSingleValue_ShouldReturnResults_WhenCategoryIsNull() {
        String markerName = "marker2";
        String value = "value2";

        List<Result> expectedResults = List.of(new Result());
        when(resultRepository.getAllByMarkerNameAndStringValue(markerName, value))
                .thenReturn(expectedResults);

        List<Result> actualResults = resultStringQueryService.getAllStringResultsForSingleValue(markerName, value, null);

        assertEquals(expectedResults, actualResults);
    }

    @Test
    void getAllStringResultsForMultipleValues_ShouldReturnResults_ForMultipleValues() {
        String markerName = "marker3";
        List<String> values = List.of("value1", "value2");
        StringCategory category = new StringCategory("category2", false, List.of("value1", "value2"));

        List<Result> results1 = List.of(new Result(), new Result());
        List<Result> results2 = List.of(new Result());

        when(resultRepository.getAllByMarkerNameAndStringValueAndStringValueCategory(markerName, "value1", "category2"))
                .thenReturn(results1);
        when(resultRepository.getAllByMarkerNameAndStringValueAndStringValueCategory(markerName, "value2", "category2"))
                .thenReturn(results2);

        List<Result> actualResults = resultStringQueryService.getAllStringResultsForMultipleValues(markerName, values, category);

        List<Result> expectedResults = new ArrayList<>();
        expectedResults.addAll(results1);
        expectedResults.addAll(results2);

        assertEquals(expectedResults, actualResults);
    }

    @Test
    void getAllStringResultsForCategory_ShouldReturnResults_WhenCategoryIsProvided() {
        String markerName = "marker4";
        StringCategory category = new StringCategory("category3", false, List.of("value1", "value2"));

        List<Result> expectedResults = List.of(new Result(), new Result());
        when(resultRepository.getAllByMarkerNameAndStringValueCategory(markerName, "category3"))
                .thenReturn(expectedResults);

        List<Result> actualResults = resultStringQueryService.getAllStringResultsForCategory(markerName, category);

        assertEquals(expectedResults, actualResults);
    }
}
