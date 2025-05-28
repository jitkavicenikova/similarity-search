package thesis.data.validation.dataset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thesis.data.enums.AggregationType;
import thesis.data.model.Record;
import thesis.data.model.*;
import thesis.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResultDataSetValidatorTest {
    private final ResultDataSetValidator validator = new ResultDataSetValidator();
    private DataSet dataSet;

    @BeforeEach
    void setUp() {
        Marker marker = new Marker();
        marker.setName("marker1");
        marker.setUnitName("unit1");

        Record record = new Record();
        record.setIdRaw(1);

        Unit unit = new Unit();
        unit.setName("unit1");

        Technology technology = new Technology();
        technology.setName("tech1");

        StringCategory stringCategory = new StringCategory();
        stringCategory.setName("category1");
        stringCategory.setValues(List.of("value1", "value2"));

        dataSet = new DataSet();
        dataSet.setMarkers(List.of(marker));
        dataSet.setRecords(List.of(record));
        dataSet.setUnits(List.of(unit));
        dataSet.setTechnologies(List.of(technology));
        dataSet.setStringCategories(List.of(stringCategory));
    }

    @Test
    void validate_Entity_ShouldPass_WhenResultIsValid() {
        Result validResult = new Result();
        validResult.setRecordIdRaw(1);
        validResult.setMarkerName("marker1");
        validResult.setTechnologyName("tech1");
        validResult.setStringValueCategory("category1");
        validResult.setStringValue("value1");

        assertDoesNotThrow(() -> validator.validateEntity(dataSet, validResult));
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenMarkerDoesNotExist() {
        Result invalidResult = new Result();
        invalidResult.setMarkerName("nonExistentMarker");

        assertThrows(ValidationException.class, () -> validator.validateEntity(dataSet, invalidResult));
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenMarkerHasChildren() {
        Marker parentMarker = new Marker();
        parentMarker.setName("parentMarker");
        parentMarker.setAggregationType(AggregationType.SUM);

        List<Marker> updatedMarkers = new ArrayList<>(dataSet.getMarkers());
        updatedMarkers.add(parentMarker);
        dataSet.setMarkers(updatedMarkers);

        Result invalidResult = new Result();
        invalidResult.setMarkerName("parentMarker");

        assertThrows(ValidationException.class, () -> validator.validateEntity(dataSet, invalidResult));
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenRecordDoesNotExist() {
        Result invalidResult = new Result();
        invalidResult.setRecordIdRaw(999);

        assertThrows(ValidationException.class, () -> validator.validateEntity(dataSet, invalidResult));
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenUnitRawDoesNotExist() {
        Result invalidResult = new Result();
        invalidResult.setUnitRawName("nonExistentUnitRaw");

        assertThrows(ValidationException.class, () -> validator.validateEntity(dataSet, invalidResult));
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenTechnologyDoesNotExist() {
        Result invalidResult = new Result();
        invalidResult.setTechnologyName("nonExistentTechnology");

        assertThrows(ValidationException.class, () -> validator.validateEntity(dataSet, invalidResult));
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenStringValueCategoryDoesNotExist() {
        Result invalidResult = new Result();
        invalidResult.setStringValueCategory("nonExistentCategory");

        assertThrows(ValidationException.class, () -> validator.validateEntity(dataSet, invalidResult));
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenStringValueNotInCategory() {
        Result invalidResult = new Result();
        invalidResult.setStringValueCategory("category1");
        invalidResult.setStringValue("nonExistentValue");

        assertThrows(ValidationException.class, () -> validator.validateEntity(dataSet, invalidResult));
    }
}
