package thesis.data.service.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Result;
import thesis.data.repository.ResultRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ResultBoolQueryServiceTest {
    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private ResultBoolQueryService resultBoolQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBoolResults_ShouldReturnResults_WhenTechnologyNamesSetIsEmpty() {
        String markerName = "marker1";
        Boolean boolValue = true;
        Set<String> emptyTechnologyNames = Set.of();
        List<Result> expectedResults = List.of(new Result(), new Result());

        when(resultRepository.getAllByMarkerNameAndBooleanValue(markerName, boolValue)).thenReturn(expectedResults);

        List<Result> actualResults = resultBoolQueryService.getAllBoolResults(markerName, boolValue, emptyTechnologyNames);

        assertEquals(expectedResults, actualResults);
    }

    @Test
    void getAllBoolResults_ShouldReturnResults_WhenTechnologyNamesSetIsNotEmpty() {
        String markerName = "marker1";
        Boolean boolValue = false;
        Set<String> technologyNames = Set.of("tech1", "tech2");

        Result result1 = new Result();
        Result result2 = new Result();
        Result result3 = new Result();
        Result result4 = new Result();

        when(resultRepository.getAllByMarkerNameAndBooleanValueAndTechnologyName(markerName, boolValue, "tech1"))
                .thenReturn(List.of(result1, result2));
        when(resultRepository.getAllByMarkerNameAndBooleanValueAndTechnologyName(markerName, boolValue, "tech2"))
                .thenReturn(List.of(result3, result4));

        List<Result> actualResults = resultBoolQueryService.getAllBoolResults(markerName, boolValue, technologyNames);

        assertEquals(Set.of(result1, result2, result3, result4), new HashSet<>(actualResults));
    }
}

