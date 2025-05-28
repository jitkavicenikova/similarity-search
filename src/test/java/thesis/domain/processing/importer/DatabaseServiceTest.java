package thesis.domain.processing.importer;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Record;
import thesis.data.model.*;
import thesis.data.service.*;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseServiceTest {
    @InjectMocks
    private DatabaseService databaseService;

    @Mock
    private MarkerService markerService;

    @Mock
    private UnitService unitService;

    @Mock
    private RecordService recordService;

    @Mock
    private ResultService resultService;

    @Mock
    private TechnologyService technologyService;

    @Mock
    private StringCategoryService stringCategoryService;

    private DataSet dataSet;
    private Record record;
    private Result result;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dataSet = new DataSet();
        dataSet.setFileName("testFile.csv");
        dataSet.setImportDate(LocalDateTime.now());

        record = new Record();
        record.setIdRaw(1);
        record.setMetadata(null);
        dataSet.setRecords(Collections.singletonList(record));

        result = new Result();
        result.setRecordIdRaw(1);
        result.setMarkerName("marker1");
        dataSet.setResults(Collections.singletonList(result));
    }

    @Test
    void saveDataSet_ShouldSaveAllEntitiesAndUpdateResultIds() {
        dataSet.setUnits(Collections.singletonList(new Unit()));
        dataSet.setMarkers(Collections.singletonList(new Marker()));
        dataSet.setTechnologies(Collections.singletonList(new Technology()));
        dataSet.setStringCategories(Collections.singletonList(new StringCategory()));

        Record savedRecord = new Record();
        savedRecord.setId("newRecordId");
        when(recordService.save(record)).thenReturn(savedRecord);

        databaseService.saveDataSet(dataSet);

        JSONObject metadataJson = new JSONObject(record.getMetadata());
        assertEquals("testFile.csv", metadataJson.getString("fileName"));
        assertNotNull(metadataJson.getString("importDate"));

        verify(unitService, times(1)).save(any(Unit.class));  // Now this should pass
        verify(markerService, times(1)).save(any(Marker.class));
        verify(technologyService, times(1)).save(any(Technology.class));
        verify(stringCategoryService, times(1)).save(any(StringCategory.class));
        verify(recordService, times(1)).save(record);

        assertEquals("newRecordId", result.getRecordId());
        assertEquals("newRecordId:marker1", result.getId());

        verify(resultService, times(1)).save(result);
    }

    @Test
    void saveDataSet_ShouldSkipSavingEntitiesInIncrement() {
        dataSet.setUnits(null);
        dataSet.setMarkers(Collections.emptyList());
        dataSet.setTechnologies(null);
        dataSet.setStringCategories(Collections.emptyList());

        result.setRecordIdRaw(1);
        Record savedRecord = new Record();
        savedRecord.setId("a");
        savedRecord.setIdRaw(1);

        when(recordService.save(any(Record.class))).thenReturn(savedRecord);

        dataSet.setResults(Collections.singletonList(result));

        databaseService.saveDataSet(dataSet);

        verify(unitService, never()).save(any());
        verify(markerService, never()).save(any());
        verify(technologyService, never()).save(any());
        verify(stringCategoryService, never()).save(any());

        verify(recordService, times(1)).save(record);
        verify(resultService, times(1)).save(result);

        assertEquals("a", result.getRecordId());
    }

    @Test
    void saveDataSet_ShouldThrowException_WhenRecordIdNotFound() {
        Record dummyRecord = new Record();
        dummyRecord.setIdRaw(-1);
        when(recordService.save(any(Record.class))).thenReturn(dummyRecord);

        dataSet.setResults(Collections.singletonList(result));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> databaseService.saveDataSet(dataSet));

        assertEquals("Record not found for ID: 1", exception.getMessage());
    }

    @Test
    void saveDataSet_ShouldSkipSavingEmptyOrNullLists() {
        dataSet = new DataSet();
        dataSet.setUnits(null);
        dataSet.setMarkers(Collections.emptyList());
        dataSet.setTechnologies(null);
        dataSet.setStringCategories(Collections.emptyList());

        databaseService.saveDataSet(dataSet);

        verify(unitService, never()).save(any());
        verify(markerService, never()).save(any());
        verify(technologyService, never()).save(any());
        verify(stringCategoryService, never()).save(any());
        verify(recordService, never()).save(any());
        verify(resultService, never()).save(any());
    }
}