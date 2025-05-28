package thesis.data.validation.dataset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thesis.data.model.Conversion;
import thesis.data.model.DataSet;
import thesis.data.model.Unit;
import thesis.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UnitDataSetValidatorTest {
    private UnitDataSetValidator validator;
    private ConversionDataSetValidator conversionDataSetValidator;
    private DataSet dataSet;

    @BeforeEach
    void setUp() {
        conversionDataSetValidator = mock(ConversionDataSetValidator.class);
        validator = new UnitDataSetValidator(conversionDataSetValidator);
        dataSet = new DataSet();
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenDuplicateConversionExists() {
        Unit unit = new Unit();
        List<Conversion> conversions = new ArrayList<>();

        Conversion conversion1 = new Conversion("unit1", "marker1", "x+2");
        Conversion conversion2 = new Conversion("unit1", "marker1", "x+2");

        conversions.add(conversion1);
        conversions.add(conversion2);

        unit.setConversions(conversions);

        assertThrows(ValidationException.class, () -> validator.validateEntity(dataSet, unit));
    }

    @Test
    void validate_Entity_ShouldPass_WhenAllConversionsAreUnique() {
        Unit unit = new Unit();
        List<Conversion> conversions = new ArrayList<>();

        Conversion conversion1 = new Conversion("unit1", "marker1", "x+2");
        Conversion conversion2 = new Conversion("unit2", "marker2", "x*3");

        conversions.add(conversion1);
        conversions.add(conversion2);

        unit.setConversions(conversions);

        validator.validateEntity(dataSet, unit);
    }

    @Test
    void validate_Entity_ShouldCallConversionDataSetValidatorForEachConversion() {
        Unit unit = new Unit();
        List<Conversion> conversions = new ArrayList<>();

        Conversion conversion1 = new Conversion("unit1", "marker1", "x+2");
        Conversion conversion2 = new Conversion("unit2", "marker2", "x*3");

        conversions.add(conversion1);
        conversions.add(conversion2);

        unit.setConversions(conversions);

        validator.validateEntity(dataSet, unit);

        verify(conversionDataSetValidator, times(1)).runValidation(dataSet, conversion1);
        verify(conversionDataSetValidator, times(1)).runValidation(dataSet, conversion2);
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenDuplicateConversionsWithDifferentInstancesExist() {
        Unit unit = new Unit();
        List<Conversion> conversions = new ArrayList<>();

        Conversion conversion1 = new Conversion("unit1", "marker1", "x+2");
        Conversion conversion2 = new Conversion("unit1", "marker1", "x+2");

        conversions.add(conversion1);
        conversions.add(conversion2);

        unit.setConversions(conversions);

        assertThrows(ValidationException.class, () -> validator.validateEntity(dataSet, unit));
    }

    @Test
    void validate_Entity_ShouldPass_WhenConversionsListIsNull() {
        Unit unit = new Unit();
        unit.setConversions(null);

        validator.validateEntity(dataSet, unit);
    }
}
