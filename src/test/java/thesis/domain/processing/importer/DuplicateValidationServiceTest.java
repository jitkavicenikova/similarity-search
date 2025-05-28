package thesis.domain.processing.importer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import thesis.data.model.Record;
import thesis.data.model.*;
import thesis.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DuplicateValidationServiceTest {
    private DuplicateValidationService duplicateValidationService;
    private DataSet dataSet;

    @BeforeEach
    void setUp() {
        duplicateValidationService = new DuplicateValidationService();
        dataSet = new DataSet();
    }

    @Test
    void validate_ShouldThrowException_WhenDuplicateMarkersExist() {
        Marker marker1 = new Marker();
        marker1.setName("Marker1");

        Marker marker2 = new Marker();
        marker2.setName("Marker1");  // Duplicate name

        dataSet.setMarkers(List.of(marker1, marker2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> duplicateValidationService.validate(dataSet));

        assertEquals("Duplicate marker with name Marker1", exception.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenDuplicateRecordsExist() {
        Record record1 = new Record();
        record1.setIdRaw(1);

        Record record2 = new Record();
        record2.setIdRaw(1);  // Duplicate id

        dataSet.setRecords(List.of(record1, record2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> duplicateValidationService.validate(dataSet));

        assertEquals("Duplicate record with id 1", exception.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenDuplicateTechnologiesExist() {
        Technology tech1 = new Technology();
        tech1.setName("Tech1");

        Technology tech2 = new Technology();
        tech2.setName("Tech1");  // Duplicate name

        dataSet.setTechnologies(List.of(tech1, tech2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> duplicateValidationService.validate(dataSet));

        assertEquals("Duplicate technology with name Tech1", exception.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenDuplicateUnitsExist() {
        Unit unit1 = new Unit();
        unit1.setName("Unit1");

        Unit unit2 = new Unit();
        unit2.setName("Unit1");  // Duplicate name

        dataSet.setUnits(List.of(unit1, unit2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> duplicateValidationService.validate(dataSet));

        assertEquals("Duplicate unit with name Unit1", exception.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenDuplicateStringCategoriesExist() {
        StringCategory category1 = new StringCategory();
        category1.setName("Category1");

        StringCategory category2 = new StringCategory();
        category2.setName("Category1");  // Duplicate name

        dataSet.setStringCategories(List.of(category1, category2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> duplicateValidationService.validate(dataSet));

        assertEquals("Duplicate stringCategory with name Category1", exception.getMessage());
    }

    @Test
    void validate_ShouldThrowException_WhenDuplicateResultsExist() {
        Result result1 = new Result();
        result1.setRecordIdRaw(1);
        result1.setMarkerName("Marker1");

        Result result2 = new Result();
        result2.setRecordIdRaw(1);
        result2.setMarkerName("Marker1");  // Duplicate pair (1, "Marker1")

        dataSet.setResults(List.of(result1, result2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> duplicateValidationService.validate(dataSet));

        assertEquals("Duplicate result with record id 1 and marker name Marker1", exception.getMessage());
    }

    @Test
    void validateIncrement_ShouldThrowException_WhenDuplicateRecordsExist() {
        Record record1 = new Record();
        record1.setIdRaw(1);

        Record record2 = new Record();
        record2.setIdRaw(1);  // Duplicate id

        dataSet.setRecords(List.of(record1, record2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> duplicateValidationService.validateIncrement(dataSet));

        assertEquals("Duplicate record with id 1", exception.getMessage());
    }

    @Test
    void validateIncrement_ShouldThrowException_WhenDuplicateResultsExist() {
        Result result1 = new Result();
        result1.setRecordIdRaw(1);
        result1.setMarkerName("Marker1");

        Result result2 = new Result();
        result2.setRecordIdRaw(1);
        result2.setMarkerName("Marker1");  // Duplicate pair (1, "Marker1")

        dataSet.setResults(List.of(result1, result2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> duplicateValidationService.validateIncrement(dataSet));

        assertEquals("Duplicate result with record id 1 and marker name Marker1", exception.getMessage());
    }
}
