package thesis.data.validation.base;

import org.junit.jupiter.api.Test;
import thesis.data.model.DeviationRange;
import thesis.data.model.TechnologyProperties;
import thesis.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TechnologyPropertiesBaseValidatorTest {

    private final TechnologyPropertiesBaseValidator validator = new TechnologyPropertiesBaseValidator() {
    };

    @Test
    void validate_ShouldPass_WhenPropertiesAreValid() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setIsPercentage(true);
        properties.setDeviationRanges(new ArrayList<>(List.of(
                new DeviationRange(0.0, 5.0, 1.0),
                new DeviationRange(6.0, 10.0, 1.0)
        )));
        properties.setSensitivity(0.9);
        properties.setSpecificity(0.95);

        validator.validate(properties);
    }

    @Test
    void validate_ShouldThrowException_WhenDeviationRangesAreSetWithoutIsPercentage() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setDeviationRanges(new ArrayList<>(List.of(
                new DeviationRange(0.0, 5.0, 1.0),
                new DeviationRange(6.0, 10.0, 1.0)
        )));

        assertThrows(ValidationException.class, () -> validator.validate(properties),
                "Deviation ranges are defined but isPercentage is not set");
    }

    @Test
    void validate_ShouldThrowException_WhenIsPercentageIsSetWithoutDeviationRanges() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setIsPercentage(true);

        assertThrows(ValidationException.class, () -> validator.validate(properties),
                "isPercentage is set but deviation ranges are not defined");
    }

    @Test
    void validate_ShouldThrowException_WhenDeviationRangesContainOverlaps() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setIsPercentage(true);
        properties.setDeviationRanges(new ArrayList<>(List.of(
                new DeviationRange(0.0, 5.0, 1.0),
                new DeviationRange(4.0, 10.0, 1.0)
        )));

        assertThrows(ValidationException.class, () -> validator.validate(properties),
                "Deviations contain overlapping ranges");
    }

    @Test
    void validate_ShouldThrowException_WhenNoRelevantFieldsAreSet() {
        TechnologyProperties properties = new TechnologyProperties();

        assertThrows(ValidationException.class, () -> validator.validate(properties),
                "At least one of deviation ranges, sensitivity, specificity, or comparableWith must be set");
    }

    @Test
    void validate_ShouldPass_WhenComparableWithIsSetWithUniqueNonNullValues() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setComparableWith(new ArrayList<>(List.of("Technology1", "Technology2")));

        validator.validate(properties);
    }

    @Test
    void validate_ShouldThrowException_WhenComparableWithContainsNullOrEmptyValues() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setComparableWith(new ArrayList<>(Arrays.asList("Technology1", "", null)));

        assertThrows(ValidationException.class, () -> validator.validate(properties),
                "comparableWith contains null or empty values");
    }

    @Test
    void validate_ShouldThrowException_WhenComparableWithContainsDuplicates() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setComparableWith(new ArrayList<>(List.of("Technology1", "Technology2", "Technology1")));

        assertThrows(ValidationException.class, () -> validator.validate(properties),
                "comparableWith contains duplicates");
    }

    @Test
    void validate_ShouldPass_WhenDeviationRangesAreSortedAndNonOverlapping() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setIsPercentage(true);
        properties.setDeviationRanges(new ArrayList<>(List.of(
                new DeviationRange(0.0, 5.0, 1.0),
                new DeviationRange(5.0, 10.0, 1.0)
        )));

        validator.validate(properties);
    }

    @Test
    void validate_ShouldPass_WhenOnlySensitivityAndSpecificityAreSet() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setSensitivity(0.9);
        properties.setSpecificity(0.95);

        validator.validate(properties);
    }
}
