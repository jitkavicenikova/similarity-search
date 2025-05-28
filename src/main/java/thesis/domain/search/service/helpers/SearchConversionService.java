package thesis.domain.search.service.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Marker;
import thesis.data.model.Unit;
import thesis.data.service.UnitService;
import thesis.domain.search.dto.NumericSearchOptions;
import thesis.domain.search.dto.RecursiveResult;
import thesis.exceptions.UnitConversionException;
import thesis.utils.FormulaEvaluator;

import java.util.List;

/**
 * Service class for converting units and values in search results.
 * It handles the conversion of recursive results and numeric search options based on the provided unit formulas.
 */
@Service
public class SearchConversionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchConversionService.class);

    private final UnitService unitService;

    @Autowired
    public SearchConversionService(UnitService unitService) {
        this.unitService = unitService;
    }

    /**
     * Converts the recursive results based on the provided child marker and parent marker unit.
     *
     * @param childMarker         the child marker for conversion
     * @param parentMarkerUnit    the parent marker unit for conversion
     * @param recursiveResults    the list of recursive results to be converted
     * @return the converted list of recursive results
     */
    public List<RecursiveResult> convertRecursiveResults(Marker childMarker, Unit parentMarkerUnit, List<RecursiveResult> recursiveResults) {
        validateMarkerUnit(childMarker, parentMarkerUnit);

        if (childMarker.getUnitName() == null || childMarker.getUnitName().equals(parentMarkerUnit.getName())) {
            return recursiveResults;
        }

        var childUnit = unitService.getEntity(childMarker.getUnitName());
        var formula = getFormula(childMarker.getName(), parentMarkerUnit.getName(), childUnit);

        recursiveResults.forEach(result -> {
            result.setMin(safelyConvert(formula, result.getMin(), false));
            result.setMax(safelyConvert(formula, result.getMax(), false));
        });

        return recursiveResults;
    }

    /**
     * Converts numeric search options based on the provided source unit and marker.
     *
     * @param sourceUnit the source unit for conversion
     * @param marker     the marker associated with the conversion
     * @param options    the numeric search options to be converted
     */
    public void convertNumericSearchOptions(Unit sourceUnit, Marker marker, NumericSearchOptions options) {
        var formula = getFormula(marker.getName(), marker.getUnitName(), sourceUnit);

        options.setValue(safelyConvert(formula, options.getValue(), true));
        options.setMinimum(safelyConvert(formula, options.getMinimum(), true));
        options.setMaximum(safelyConvert(formula, options.getMaximum(), true));
        options.setAbsoluteDeviation(safelyConvert(formula, options.getAbsoluteDeviation(), true));
        options.setAbsoluteTolerance(safelyConvert(formula, options.getAbsoluteTolerance(), true));
    }

    private String getFormula(String markerName, String targetUnitName, Unit sourceUnit) {
        if (sourceUnit.getConversions() == null) {
            throw new UnitConversionException(String.format("Unit '%s' does not have any conversions", sourceUnit.getName()));
        }

        var conversion = sourceUnit.getConversions().stream()
                .filter(c -> c.getTargetUnitName().equals(targetUnitName)
                        && c.getMarkerName().equals(markerName))
                .findFirst()
                .orElseThrow(() -> new UnitConversionException(String.format("Conversion between from unit '%s' to target unit '%s' does not exist for marker '%s'", sourceUnit.getName(), targetUnitName, markerName)));

        return conversion.getFormula();
    }

    private Double safelyConvert(String formula, Double value, Boolean failOnError) {
        if (value == null) {
            return null;
        }

        try {
            return FormulaEvaluator.evaluateFormula(formula, value);
        } catch (Exception e) {
            LOGGER.error("Failed to convert value", e);
            if (failOnError) {
                throw new UnitConversionException(String.format(
                        "Conversion failed for value '%s' using formula '%s'. Error: %s",
                        value, formula, e.getMessage()
                ));
            }
            return Double.NaN;
        }
    }

    private void validateMarkerUnit(Marker childMarker, Unit parentMarkerUnit) {
        if (parentMarkerUnit == null && childMarker.getUnitName() != null) {
            throw new UnitConversionException(String.format("Parent marker unit is null, but marker '%s' unit is not", childMarker.getName()));
        }

        if (parentMarkerUnit != null && childMarker.getUnitName() == null) {
            throw new UnitConversionException(String.format("Parent marker unit is not null, but marker '%s' unit is", childMarker.getName()));
        }
    }
}
