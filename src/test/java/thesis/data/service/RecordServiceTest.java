package thesis.data.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Record;
import thesis.data.repository.RecordRepository;
import thesis.data.repository.ResultRepository;
import thesis.exceptions.EntityInUseException;
import thesis.exceptions.EntityNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecordServiceTest {
    @Mock
    private RecordRepository recordRepository;

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private RecordService recordService;

    public RecordServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveRecord() {
        Record record = new Record();
        when(recordRepository.save(record)).thenReturn(record);

        Record savedRecord = recordService.save(record);

        assertNotNull(savedRecord);
        verify(recordRepository, times(1)).save(record);
    }

    @Test
    void getEntity_ShouldReturnRecord_WhenRecordExists() {
        Record record = new Record();
        record.setId("record1");
        when(recordRepository.findById("record1")).thenReturn(Optional.of(record));

        Record foundRecord = recordService.getEntity("record1");

        assertNotNull(foundRecord);
        assertEquals("record1", foundRecord.getId());
        verify(recordRepository, times(1)).findById("record1");
    }

    @Test
    void getEntity_ShouldThrowException_WhenRecordDoesNotExist() {
        when(recordRepository.findById("record1")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> recordService.getEntity("record1"));
        assertEquals("Record with id 'record1' not found", exception.getMessage());
    }

    @Test
    void existsById_ShouldReturnTrue_WhenRecordExists() {
        when(recordRepository.existsById("record1")).thenReturn(true);

        assertTrue(recordService.existsById("record1"));
    }

    @Test
    void existsById_ShouldReturnFalse_WhenRecordDoesNotExist() {
        when(recordRepository.existsById("record1")).thenReturn(false);

        assertFalse(recordService.existsById("record1"));
    }

    @Test
    void update_ShouldUpdateRecord_WhenRecordExists() {
        Record dbRecord = new Record();
        dbRecord.setId("record1");
        dbRecord.setMetadata("old metadata");

        Record updatedRecord = new Record();
        updatedRecord.setMetadata("new metadata");

        when(recordRepository.findById("record1")).thenReturn(Optional.of(dbRecord));
        when(recordRepository.save(dbRecord)).thenReturn(dbRecord);

        Record result = recordService.update("record1", updatedRecord);

        assertNotNull(result);
        assertEquals("new metadata", result.getMetadata());
        verify(recordRepository, times(1)).save(dbRecord);
    }

    @Test
    void update_ShouldThrowException_WhenRecordDoesNotExist() {
        Record updatedRecord = new Record();
        updatedRecord.setMetadata("new metadata");

        when(recordRepository.findById("record1")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> recordService.update("record1", updatedRecord));
        assertEquals("Record with id 'record1' not found", exception.getMessage());
    }

    @Test
    void delete_ShouldDeleteRecord_WhenNoDependenciesExist() {
        when(recordRepository.existsById("record1")).thenReturn(true);
        when(resultRepository.existsByRecordId("record1")).thenReturn(false);

        recordService.delete("record1");

        verify(recordRepository).deleteById("record1");
    }

    @Test
    void delete_ShouldThrowException_WhenRecordDoesNotExist() {
        when(recordRepository.existsById("record1")).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> recordService.delete("record1"));
        assertEquals("Record with id 'record1' not found", exception.getMessage());
    }

    @Test
    void delete_ShouldThrowException_WhenRecordIsUsedInResults() {
        when(recordRepository.existsById("record1")).thenReturn(true);
        when(resultRepository.existsByRecordId("record1")).thenReturn(true);

        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> recordService.delete("record1"));
        assertEquals("Cannot delete record with id 'record1' because it is used in results", exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnAllRecords() {
        Iterable<Record> records = mock(Iterable.class);
        when(recordRepository.findAll()).thenReturn(records);

        Iterable<Record> result = recordService.findAll();

        assertNotNull(result);
        verify(recordRepository, times(1)).findAll();
    }
}