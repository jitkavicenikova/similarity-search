package thesis.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Result;
import thesis.data.repository.ResultRepository;
import thesis.data.repository.TechnologyRepository;
import thesis.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultServiceTest {
    @Mock
    private ResultRepository resultRepository;

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private ResultService resultService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveResultWithDeviations_WhenResultHasTechnologyWithAbsoluteDeviation() {
        var result = new Result();
        result.setTechnologyName("Tech1");
        result.setMarkerName("Marker1");
        result.setMin(5.0);
        result.setMax(10.0);

        when(technologyRepository.isDeviationPercentage("Tech1", "Marker1")).thenReturn(false);
        when(technologyRepository.getFromDeviation("Tech1", "Marker1", 5.0)).thenReturn(Set.of("1.0::10.0::2.0"));
        when(technologyRepository.getToDeviation("Tech1", "Marker1", 5.0)).thenReturn(Set.of("1.0::10.0::2.0"));
        when(technologyRepository.getFromDeviation("Tech1", "Marker1", 10.0)).thenReturn(Set.of("1.0::10.0::2.0"));
        when(technologyRepository.getToDeviation("Tech1", "Marker1", 10.0)).thenReturn(Set.of("1.0::10.0::2.0"));

        resultService.save(result);

        verify(resultRepository).saveResultSearchIndexWithTechDeviations(result, 3.0, 12.0);
        verify(resultRepository).save(result);
    }

    @Test
    void save_ShouldSaveResultWithDeviations_WhenResultHasTechnologyWithPercentageDeviation() {
        var result = new Result();
        result.setTechnologyName("Tech1");
        result.setMarkerName("Marker1");
        result.setMin(5.0);
        result.setMax(10.0);

        when(technologyRepository.isDeviationPercentage("Tech1", "Marker1")).thenReturn(true);
        when(technologyRepository.getFromDeviation("Tech1", "Marker1", 5.0)).thenReturn(Set.of("1.0::10.0::2.0"));
        when(technologyRepository.getToDeviation("Tech1", "Marker1", 5.0)).thenReturn(Set.of("1.0::10.0::2.0"));
        when(technologyRepository.getFromDeviation("Tech1", "Marker1", 10.0)).thenReturn(Set.of("1.0::10.0::2.0"));
        when(technologyRepository.getToDeviation("Tech1", "Marker1", 10.0)).thenReturn(Set.of("1.0::10.0::2.0"));

        resultService.save(result);

        verify(resultRepository).saveResultSearchIndexWithTechDeviations(result, 4.9, 10.2);
        verify(resultRepository).save(result);
    }

    @Test
    void save_ShouldSaveResultWithoutDeviations_WhenResultHasNoTechnology() {
        var result = new Result();
        result.setMin(5.0);
        result.setMax(10.0);

        resultService.save(result);

        verify(resultRepository, never()).saveResultSearchIndexWithTechDeviations(any(), any(), any());
        verify(resultRepository).saveResultSearchIndex(result);
        verify(resultRepository).save(result);
    }

    @Test
    void getEntity_ShouldReturnResult_WhenResultExists() {
        var result = new Result();
        result.setId("1");

        when(resultRepository.findById("1")).thenReturn(Optional.of(result));

        var retrievedResult = resultService.getEntity("1");
        assertEquals(result, retrievedResult);
    }

    @Test
    void getEntity_ShouldThrowException_WhenResultDoesNotExist() {
        when(resultRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> resultService.getEntity("1"));
    }

    @Test
    void update_ShouldUpdateAndSaveResultWithDeviations_WhenResultExists() {
        var existingResult = new Result();
        existingResult.setId("1");
        existingResult.setMin(3.0);
        existingResult.setMax(6.0);
        existingResult.setTechnologyName("Tech1");
        existingResult.setMarkerName("Marker1");

        var updatedResult = new Result();
        updatedResult.setMin(5.0);
        updatedResult.setMax(10.0);
        updatedResult.setTechnologyName("Tech1");
        updatedResult.setMarkerName("Marker1");

        when(resultRepository.findById("1")).thenReturn(Optional.of(existingResult));
        when(technologyRepository.isDeviationPercentage("Tech1", "Marker1")).thenReturn(false);
        when(technologyRepository.getFromDeviation("Tech1", "Marker1", 5.0)).thenReturn(Set.of("1.0::10.0::2.0"));
        when(technologyRepository.getToDeviation("Tech1", "Marker1", 5.0)).thenReturn(Set.of("1.0::10.0::2.0"));
        when(technologyRepository.getFromDeviation("Tech1", "Marker1", 10.0)).thenReturn(Set.of("1.0::10.0::2.0"));
        when(technologyRepository.getToDeviation("Tech1", "Marker1", 10.0)).thenReturn(Set.of("1.0::10.0::2.0"));

        resultService.update("1", updatedResult);

        verify(resultRepository).deleteResultSearchIndex(existingResult);
        verify(resultRepository).deleteResultSearchIndexWithTechDeviations(existingResult);
        verify(resultRepository).saveResultSearchIndexWithTechDeviations(existingResult, 3.0, 12.0);
        verify(resultRepository).save(existingResult);
    }

    @Test
    void delete_ShouldDeleteResultAndIndexes_WhenResultExists() {
        var result = new Result();
        result.setId("1");
        result.setMin(5.0);

        when(resultRepository.findById("1")).thenReturn(Optional.of(result));

        resultService.delete("1");

        verify(resultRepository).deleteResultSearchIndex(result);
        verify(resultRepository).deleteResultSearchIndexWithTechDeviations(result);
        verify(resultRepository).deleteById("1");
    }

    @Test
    void getResultsByRecordId_ShouldReturnResultsList_WhenResultsExist() {
        var result1 = new Result();
        result1.setRecordId("record123");
        var result2 = new Result();
        result2.setRecordId("record123");

        when(resultRepository.getAllByRecordId("record123")).thenReturn(List.of(result1, result2));

        var results = resultService.getResultsByRecordId("record123");

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("record123", results.get(0).getRecordId());
        assertEquals("record123", results.get(1).getRecordId());
        verify(resultRepository).getAllByRecordId("record123");
    }

    @Test
    void findAll_ShouldReturnAllResults_WhenResultsExist() {
        var result1 = new Result();
        var result2 = new Result();

        when(resultRepository.findAll()).thenReturn(List.of(result1, result2));

        var results = resultService.findAll();

        assertNotNull(results);
        assertEquals(2, ((List<Result>) results).size());
        verify(resultRepository).findAll();
    }
}
