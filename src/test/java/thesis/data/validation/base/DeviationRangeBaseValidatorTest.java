package thesis.data.validation.base;

import org.junit.jupiter.api.Test;
import thesis.data.model.DeviationRange;
import thesis.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DeviationRangeBaseValidatorTest {
    private final DeviationRangeBaseValidator validator = new DeviationRangeBaseValidator() {
    };

    @Test
    void validate_ShouldThrowException_WhenFromIsGreaterThanTo() {
        var deviationRange = new DeviationRange();
        deviationRange.setFrom(10.0);
        deviationRange.setTo(5.0);

        assertThrows(ValidationException.class, () -> validator.validate(deviationRange),
                "Expected ValidationException when 'from' is greater than 'to'");
    }

    @Test
    void validate_ShouldPass_WhenFromIsLessThanOrEqualToTo() {
        var deviationRange = new DeviationRange();
        deviationRange.setFrom(5.0);
        deviationRange.setTo(10.0);

        validator.validate(deviationRange);
    }

    @Test
    void validate_ShouldPass_WhenFromEqualsTo() {
        var deviationRange = new DeviationRange();
        deviationRange.setFrom(7.0);
        deviationRange.setTo(7.0);

        validator.validate(deviationRange);
    }
}