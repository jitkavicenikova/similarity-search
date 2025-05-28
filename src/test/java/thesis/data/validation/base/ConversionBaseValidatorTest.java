package thesis.data.validation.base;

import org.junit.jupiter.api.Test;
import thesis.data.model.Conversion;
import thesis.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConversionBaseValidatorTest {
    private final ConversionBaseValidator validator = new ConversionBaseValidator() {
    };

    @Test
    void validate_ShouldThrowException_WhenFormulaIsInvalid() {
        var conversion = new Conversion();
        conversion.setFormula("invalidFormula");

        assertThrows(ValidationException.class, () -> validator.validate(conversion));
    }

    @Test
    void validate_ShouldPass_WhenFormulaIsValid() {
        var conversion = new Conversion();
        conversion.setFormula("1+x");

        validator.validate(conversion);
    }
}