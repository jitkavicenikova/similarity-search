package thesis.data.validation.dataset;

import org.junit.jupiter.api.Test;
import thesis.data.model.Conversion;
import thesis.data.model.DataSet;
import thesis.data.model.Marker;
import thesis.data.model.Unit;
import thesis.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConversionDataSetValidatorTest {
    private final ConversionDataSetValidator conversionDataSetValidator = new ConversionDataSetValidator();

    @Test
    void validate_Entity_ShouldPass_WhenMarkerAndUnitExistInDataSet() {
        Conversion conversion = new Conversion("unit1", "marker1", "x+1");
        Marker marker = new Marker();
        marker.setName("marker1");

        Unit unit = new Unit();
        unit.setName("unit1");

        DataSet dataSet = new DataSet();
        dataSet.setMarkers(List.of(marker));
        dataSet.setUnits(List.of(unit));

        conversionDataSetValidator.validateEntity(dataSet, conversion);
    }

    @Test
    void validate_Entity_ShouldThrowValidationException_WhenTargetUnitNotFoundInDataSet() {
        Conversion conversion = new Conversion("unit1", "marker1", "x+1");
        Marker marker = new Marker();
        marker.setName("marker1");

        DataSet dataSet = new DataSet();
        dataSet.setMarkers(List.of(marker));
        dataSet.setUnits(List.of()); // No matching unit

        assertThrows(ValidationException.class,
                () -> conversionDataSetValidator.validateEntity(dataSet, conversion),
                "Conversion target unit with name unit1 not found");
    }

    @Test
    void validate_Entity_ShouldThrowValidationException_WhenMarkerNotFoundInDataSet() {
        Conversion conversion = new Conversion("unit1", "marker1", "x+1");
        Unit unit = new Unit();
        unit.setName("unit1");

        DataSet dataSet = new DataSet();
        dataSet.setMarkers(List.of()); // No matching marker
        dataSet.setUnits(List.of(unit));

        assertThrows(ValidationException.class,
                () -> conversionDataSetValidator.validateEntity(dataSet, conversion),
                "Conversion marker with name marker1 not found");
    }

    @Test
    void validate_Entity_ShouldPass_WhenDataSetContainsMultipleMarkersAndUnitsWithMatchingNames() {
        Conversion conversion = new Conversion("unit1", "marker1", "x+1");

        Marker marker1 = new Marker();
        marker1.setName("marker1");

        Marker marker2 = new Marker();
        marker2.setName("marker2");

        Unit unit1 = new Unit();
        unit1.setName("unit1");

        Unit unit2 = new Unit();
        unit2.setName("unit2");

        DataSet dataSet = new DataSet();
        dataSet.setMarkers(List.of(marker1, marker2));
        dataSet.setUnits(List.of(unit1, unit2));

        conversionDataSetValidator.validateEntity(dataSet, conversion);
    }
}
