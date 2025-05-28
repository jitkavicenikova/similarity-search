package thesis.data.validation.dataset;

import org.junit.jupiter.api.Test;
import thesis.data.enums.AggregationType;
import thesis.data.model.DataSet;
import thesis.data.model.Marker;
import thesis.data.model.Unit;
import thesis.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MarkerDataSetValidatorTest {
    private final MarkerDataSetValidator markerDataSetValidator = new MarkerDataSetValidator();

    @Test
    void validate_Entity_ShouldPass_WhenUnitExistsAndChildMarkersHaveMatchingAggregationTypes() {
        Marker marker = new Marker("marker1", "desc", "raw", "unit1", List.of("childMarker1"), AggregationType.SUM);
        marker.setChildMarkerNames(List.of("childMarker1"));
        Marker childMarker = new Marker("childMarker1", "unit1", "raw", null);

        Unit unit = new Unit();
        unit.setName("unit1");

        DataSet dataSet = new DataSet();
        dataSet.setMarkers(List.of(childMarker));
        dataSet.setUnits(List.of(unit));

        markerDataSetValidator.validateEntity(dataSet, marker);
    }

    @Test
    void validate_Entity_ShouldThrowValidationException_WhenUnitDoesNotExistInDataSet() {
        Marker marker = new Marker("marker1", "desc", null, "unit1");

        DataSet dataSet = new DataSet();

        assertThrows(ValidationException.class,
                () -> markerDataSetValidator.validateEntity(dataSet, marker),
                "Unit with name unit1 does not exist");
    }

    @Test
    void validate_Entity_ShouldPass_WhenMarkerHasNoUnitName() {
        Marker marker = new Marker("marker1", null, null, null);
        DataSet dataSet = new DataSet();

        markerDataSetValidator.validateEntity(dataSet, marker);
    }

    @Test
    void validate_Entity_ShouldThrowValidationException_WhenChildMarkerDoesNotExist() {
        Marker marker = new Marker("marker1", "desc", "raw", "unit1", List.of("childMarker1"), AggregationType.SUM);

        DataSet dataSet = new DataSet();
        dataSet.setMarkers(List.of()); // No markers

        assertThrows(ValidationException.class,
                () -> markerDataSetValidator.validateEntity(dataSet, marker),
                "Child marker with name nonExistingChild does not exist");
    }

    @Test
    void validate_Entity_ShouldThrowValidationException_WhenChildMarkerHasDifferentAggregationType() {
        Marker marker = new Marker("marker1", "desc", "raw", "unit1", List.of("childMarker1"), AggregationType.SUM);
        Marker childMarker = new Marker("childMarker1", "desc", "raw", "unit1", List.of("childMarker3"), AggregationType.AVERAGE);

        DataSet dataSet = new DataSet();
        dataSet.setMarkers(List.of(childMarker));

        assertThrows(ValidationException.class,
                () -> markerDataSetValidator.validateEntity(dataSet, marker),
                "Child marker with name childMarker1 has a different aggregation type");
    }

    @Test
    void validate_Entity_ShouldPass_WhenMarkerHasNoChildMarkers() {
        Marker marker = new Marker("marker1", "desc", null, "unit1");

        Unit unit = new Unit();
        unit.setName("unit1");

        DataSet dataSet = new DataSet();
        dataSet.setUnits(List.of(unit));

        markerDataSetValidator.validateEntity(dataSet, marker);
    }
}
