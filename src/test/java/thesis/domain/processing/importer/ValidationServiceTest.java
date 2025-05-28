package thesis.domain.processing.importer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Record;
import thesis.data.model.*;
import thesis.data.validation.database.RecordDatabaseValidator;
import thesis.data.validation.database.ResultDatabaseValidator;
import thesis.data.validation.dataset.*;
import thesis.exceptions.ValidationException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

class ValidationServiceTest {
    @InjectMocks
    private ValidationService validationService;

    @Mock
    private DuplicateValidationService duplicateValidationService;
    @Mock
    private UnitDataSetValidator unitDataSetValidator;
    @Mock
    private TechnologyDataSetValidator technologyDataSetValidator;
    @Mock
    private MarkerDataSetValidator markerDataSetValidator;
    @Mock
    private RecordDataSetValidator recordDataSetValidator;
    @Mock
    private ResultDataSetValidator resultDataSetValidator;
    @Mock
    private StringCategoryDataSetValidator stringCategoryDataSetValidator;
    @Mock
    private RecordDatabaseValidator recordDatabaseValidator;
    @Mock
    private ResultDatabaseValidator resultDatabaseValidator;

    private DataSet dataSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataSet = new DataSet();
    }

    @Test
    void validate_ShouldRunAllEntityValidationsAndCheckDuplicates() {
        dataSet.setUnits(List.of(new Unit()));
        dataSet.setMarkers(List.of(new Marker()));
        dataSet.setTechnologies(List.of(new Technology()));
        dataSet.setRecords(List.of(new Record()));
        dataSet.setStringCategories(List.of(new StringCategory()));
        dataSet.setResults(List.of(new Result()));

        validationService.validate(dataSet);

        verify(unitDataSetValidator).runValidation(dataSet, dataSet.getUnits().get(0));
        verify(markerDataSetValidator).runValidation(dataSet, dataSet.getMarkers().get(0));
        verify(technologyDataSetValidator).runValidation(dataSet, dataSet.getTechnologies().get(0));
        verify(recordDataSetValidator).runValidation(dataSet, dataSet.getRecords().get(0));
        verify(stringCategoryDataSetValidator).runValidation(dataSet, dataSet.getStringCategories().get(0));
        verify(resultDataSetValidator).runValidation(dataSet, dataSet.getResults().get(0));
        verify(duplicateValidationService).validate(dataSet);
    }

    @Test
    void validate_ShouldNotThrowException_WhenListsAreNull() {
        dataSet.setUnits(null);
        dataSet.setMarkers(null);
        dataSet.setTechnologies(null);
        dataSet.setRecords(null);
        dataSet.setStringCategories(null);
        dataSet.setResults(null);

        validationService.validate(dataSet);
    }

    @Test
    void validateIncrement_ShouldThrowException_WhenForbiddenEntitiesArePresent() {
        dataSet.setUnits(List.of(new Unit()));

        ValidationException exception = assertThrows(ValidationException.class, () -> validationService.validateIncrement(dataSet));
        assertThrows(ValidationException.class, () -> validationService.validateIncrement(dataSet));
        assert exception.getMessage().contains("Units, Markers, Technologies and StringCategories cannot be uploaded in increment");
    }

    @Test
    void validateIncrement_ShouldRunDatabaseValidationsAndCheckDuplicates() {
        Record record = new Record();
        record.setIdRaw(1);

        Result result = new Result();
        result.setRecordIdRaw(1);

        dataSet.setRecords(List.of(record));
        dataSet.setResults(List.of(result));

        validationService.validateIncrement(dataSet);

        verify(recordDatabaseValidator).runValidation(record);
        verify(resultDatabaseValidator).runValidation(result);
        verify(duplicateValidationService).validateIncrement(dataSet);
    }

    @Test
    void validateIncrement_ShouldThrowException_WhenResultRefersToNonExistentRecord() {
        Result result = new Result();
        result.setRecordIdRaw(2);

        dataSet.setResults(List.of(result));

        ValidationException exception = assertThrows(ValidationException.class, () -> validationService.validateIncrement(dataSet));
        assert exception.getMessage().equals("Record with id 2 does not exist");
    }

    @Test
    void validateIncrement_ShouldNotThrowException_WhenListsAreEmpty() {
        dataSet.setRecords(Collections.emptyList());
        dataSet.setResults(Collections.emptyList());

        validationService.validateIncrement(dataSet);
    }
}
