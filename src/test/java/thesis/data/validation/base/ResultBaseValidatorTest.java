package thesis.data.validation.base;

import org.junit.jupiter.api.Test;
import thesis.data.model.Result;
import thesis.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ResultBaseValidatorTest {
    private final ResultBaseValidator validator = new ResultBaseValidator() {
    };

    @Test
    void validate_ShouldThrowException_WhenStringValueCategoryIsSetWithoutStringValue() {
        var result = new Result();
        result.setStringValueCategory("Category");

        assertThrows(ValidationException.class, () -> validator.validate(result),
                "Expected ValidationException when stringValueCategory is set without stringValue");
    }

    @Test
    void validate_ShouldThrowException_WhenMinIsGreaterThanMax() {
        var result = new Result();
        result.setMin(10.0);
        result.setMax(5.0);

        assertThrows(ValidationException.class, () -> validator.validate(result),
                "Expected ValidationException when min is greater than max");
    }

    @Test
    void validate_ShouldThrowException_WhenNoValuesAreSet() {
        var result = new Result();

        assertThrows(ValidationException.class, () -> validator.validate(result),
                "Expected ValidationException when no values are set (min/max, stringValue, or booleanValue)");
    }

    @Test
    void validate_ShouldThrowException_WhenMultipleValuesAreSet() {
        var result = new Result();
        result.setMin(1.0);
        result.setStringValue("Valid String");

        assertThrows(ValidationException.class, () -> validator.validate(result),
                "Expected ValidationException when more than one value is set");
    }

    @Test
    void validate_ShouldPass_WhenOnlyMinAndMaxAreSetCorrectly() {
        var result = new Result();
        result.setMin(1.0);
        result.setMax(10.0);

        validator.validate(result);
    }

    @Test
    void validate_ShouldPass_WhenOnlyStringValueIsSet() {
        var result = new Result();
        result.setStringValue("Some Value");

        validator.validate(result);
    }

    @Test
    void validate_ShouldPass_WhenOnlyBooleanValueIsSet() {
        var result = new Result();
        result.setBooleanValue(true);

        validator.validate(result);
    }
}
