package thesis.domain.search.service.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Conversion;
import thesis.data.model.Marker;
import thesis.data.model.Unit;
import thesis.data.service.UnitService;
import thesis.domain.search.dto.NumericSearchOptions;
import thesis.domain.search.dto.RecursiveResult;
import thesis.exceptions.UnitConversionException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SearchConversionServiceTest {
    @Mock
    private UnitService unitService;

    @InjectMocks
    private SearchConversionService conversionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void convertRecursiveResults_ShouldReturnSameList_WhenChildUnitIsNullOrMatchesParentUnit() {
        Marker marker = new Marker();
        marker.setUnitName("mg/L");

        Unit parentUnit = new Unit();
        parentUnit.setName("mg/L");

        RecursiveResult result1 = new RecursiveResult("id", 1.0, 5.0);
        RecursiveResult result2 = new RecursiveResult("id", 2.0, 6.0);

        List<RecursiveResult> recursiveResults = List.of(result1, result2);

        List<RecursiveResult> convertedResults = conversionService.convertRecursiveResults(marker, parentUnit, recursiveResults);

        assertEquals(recursiveResults, convertedResults);
    }

    @Test
    void convertRecursiveResults_ShouldConvertValues_WhenUnitsDiffer() {
        Marker marker = new Marker();
        marker.setName("Glucose");
        marker.setUnitName("mmol/L");

        Unit parentUnit = new Unit();
        parentUnit.setName("mg/dL");

        Unit childUnit = new Unit();
        childUnit.setName("mmol/L");
        childUnit.setConversions(List.of(new Conversion("mg/dL", "Glucose", "x * 18")));

        when(unitService.getEntity("mmol/L")).thenReturn(childUnit);

        RecursiveResult result = new RecursiveResult("id", 5.0, 10.0);
        List<RecursiveResult> results = List.of(result);

        List<RecursiveResult> convertedResults = conversionService.convertRecursiveResults(marker, parentUnit, results);

        assertNotNull(convertedResults);
        assertEquals(90.0, convertedResults.get(0).getMin());  // 5.0 * 18 = 90.0
        assertEquals(180.0, convertedResults.get(0).getMax()); // 10.0 * 18 = 180.0
    }

    @Test
    void convertNumericConfig_ShouldConvertValuesUsingFormula() {
        Unit sourceUnit = new Unit();
        sourceUnit.setName("mmol/L");
        sourceUnit.setConversions(List.of(new Conversion("mg/dL", "Glucose", "x * 18")));

        Marker marker = new Marker();
        marker.setName("Glucose");
        marker.setUnitName("mg/dL");

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(4.0);
        options.setMaximum(6.0);

        conversionService.convertNumericSearchOptions(sourceUnit, marker, options);

        assertEquals(72.0, options.getMinimum());  // 4.0 * 18 = 72.0
        assertEquals(108.0, options.getMaximum()); // 6.0 * 18 = 108.0
    }

    @Test
    void convertNumericConfig_ShouldThrowException_WhenConversionFormulaIsMissing() {
        Unit sourceUnit = new Unit();
        sourceUnit.setName("mmol/L");
        sourceUnit.setConversions(null);  // No conversions available

        Marker marker = new Marker();
        marker.setName("Glucose");
        marker.setUnitName("mg/dL");

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(4.0);

        assertThrows(UnitConversionException.class, () -> conversionService.convertNumericSearchOptions(sourceUnit, marker, options));
    }

    @Test
    void convertRecursiveResults_ShouldNotModifyValues_WhenChildUnitEqualsParentUnit() {
        Marker marker = new Marker();
        marker.setUnitName("unit1");

        Unit parentUnit = new Unit();
        parentUnit.setName("unit1");

        RecursiveResult result = new RecursiveResult("id", 5.0, 10.0);

        List<RecursiveResult> convertedResults = conversionService.convertRecursiveResults(marker, parentUnit, List.of(result));

        assertEquals(5.0, convertedResults.get(0).getMin());
        assertEquals(10.0, convertedResults.get(0).getMax());
    }

    @Test
    void convertRecursiveResults_ShouldReturnNaN_WhenDivisionByZeroOccursAndFailOnErrorIsFalse() {
        Marker marker = new Marker();
        marker.setUnitName("unit1");
        marker.setName("markerName");

        Unit parentUnit = new Unit();
        parentUnit.setName("unit2");

        RecursiveResult result = new RecursiveResult("id", 5.0, 10.0);

        Unit sourceUnit = new Unit();
        sourceUnit.setName("unit1");
        sourceUnit.setConversions(List.of(new Conversion("unit2", "markerName", "x / 0")));

        when(unitService.getEntity("unit1")).thenReturn(sourceUnit);

        List<RecursiveResult> convertedResults = conversionService.convertRecursiveResults(marker, parentUnit, List.of(result));

        assertTrue(convertedResults.get(0).getMin().isNaN(), "Expected min to be NaN due to division by zero");
        assertTrue(convertedResults.get(0).getMax().isNaN(), "Expected max to be NaN due to division by zero");
    }

    @Test
    void convertNumericConfig_ShouldThrowException_WhenFormulaFailsAndFailOnErrorIsTrue() {
        Marker marker = new Marker();
        marker.setName("markerName");
        marker.setUnitName("unit2");

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(5.0);

        Unit sourceUnit = new Unit();
        sourceUnit.setName("unit1");
        sourceUnit.setConversions(List.of(new Conversion("unit2", "markerName", "x / 0")));  // Division by zero to cause failure

        when(unitService.getEntity("unit1")).thenReturn(sourceUnit);

        assertThrows(UnitConversionException.class, () ->
                conversionService.convertNumericSearchOptions(sourceUnit, marker, options));
    }

    @Test
    void validateMarkerUnit_ShouldThrowException_WhenParentUnitIsNullAndMarkerUnitIsNot() {
        Marker marker = new Marker();
        marker.setUnitName("mg/L");

        Unit parentUnit = null;

        assertThrows(UnitConversionException.class, () -> conversionService.convertRecursiveResults(marker, parentUnit, List.of()));
    }

    @Test
    void validateMarkerUnit_ShouldThrowException_WhenParentUnitIsNotNullAndMarkerUnitIs() {
        Marker marker = new Marker();
        marker.setUnitName(null);

        Unit parentUnit = new Unit();
        parentUnit.setName("mg/L");

        assertThrows(UnitConversionException.class, () -> conversionService.convertRecursiveResults(marker, parentUnit, List.of()));
    }
}

