package thesis.data.validation.base;

import org.junit.jupiter.api.Test;
import thesis.data.model.StringCategory;
import thesis.exceptions.ValidationException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StringCategoryBaseValidatorTest {
    private final StringCategoryBaseValidator validator = new StringCategoryBaseValidator() {
    };

    @Test
    void validate_ShouldPass_WhenValuesAreValid() {
        StringCategory category = new StringCategory();
        category.setValues(List.of("value1", "value2", "value3"));

        validator.validate(category);
    }

    @Test
    void validate_ShouldThrowException_WhenValuesContainNullOrBlank() {
        StringCategory category = new StringCategory();
        category.setValues(Arrays.asList("value1", " ", null));

        assertThrows(ValidationException.class, () -> validator.validate(category),
                "values contain null or empty values");
    }

    @Test
    void validate_ShouldThrowException_WhenDuplicateValuesArePresent() {
        StringCategory category = new StringCategory();
        category.setValues(List.of("value1", "value2", "value1"));

        assertThrows(ValidationException.class, () -> validator.validate(category),
                "Duplicate values found in string category values");
    }
}
