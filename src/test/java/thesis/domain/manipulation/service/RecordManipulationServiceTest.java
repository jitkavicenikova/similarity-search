package thesis.domain.manipulation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.mapper.RecordMapper;
import thesis.data.model.Record;
import thesis.data.model.Result;
import thesis.data.service.RecordService;
import thesis.data.service.ResultService;
import thesis.data.validation.database.RecordDatabaseValidator;
import thesis.domain.manipulation.dto.RecordUpdateDto;
import thesis.exceptions.EntityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecordManipulationServiceTest {

    @Mock
    private RecordService recordService;

    @Mock
    private ResultService resultService;

    @Mock
    private RecordDatabaseValidator recordDatabaseValidator;

    @Mock
    private RecordMapper recordMapper;

    @InjectMocks
    private RecordManipulationService recordManipulationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteWithResults_ShouldDeleteRecordAndResults_WhenRecordExists() {
        when(recordService.existsById("recordId")).thenReturn(true);
        Result result1 = new Result();
        result1.setId("resultId1");
        when(resultService.getResultsByRecordId("recordId")).thenReturn(List.of(result1));

        recordManipulationService.deleteWithResults("recordId");

        verify(resultService).getResultsByRecordId("recordId");
        verify(resultService).delete("resultId1");
        verify(recordService).delete("recordId");
    }

    @Test
    void deleteWithResults_ShouldThrowEntityNotFoundException_WhenRecordDoesNotExist() {
        when(recordService.existsById("nonExistentId")).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> recordManipulationService.deleteWithResults("nonExistentId"));

        assertEquals("Record with id 'nonExistentId' not found", exception.getMessage());
    }

    @Test
    void update_ShouldCallRecordServiceUpdate_WhenValidData() {
        String id = "record123";
        RecordUpdateDto recordUpdateDto = new RecordUpdateDto(1, null);
        Record updatedRecord = new Record();
        updatedRecord.setId(id);

        when(recordMapper.mapFromUpdateDto(recordUpdateDto)).thenReturn(updatedRecord);
        when(recordService.update(id, updatedRecord)).thenReturn(updatedRecord);

        Record result = recordManipulationService.update(id, recordUpdateDto);

        verify(recordMapper).mapFromUpdateDto(recordUpdateDto);
        verify(recordService).update(id, updatedRecord);
        assertEquals(id, result.getId());
    }

    @Test
    void save_ShouldCallRecordServiceSave_WhenValidData() {
        RecordUpdateDto recordUpdateDto = new RecordUpdateDto(1, null);
        Record savedRecord = new Record();
        savedRecord.setId("newRecordId");

        when(recordMapper.mapFromUpdateDto(recordUpdateDto)).thenReturn(savedRecord);
        when(recordService.save(savedRecord)).thenReturn(savedRecord);

        Record result = recordManipulationService.save(recordUpdateDto);

        verify(recordMapper).mapFromUpdateDto(recordUpdateDto);
        verify(recordService).save(savedRecord);
        assertEquals("newRecordId", result.getId());
    }
}
