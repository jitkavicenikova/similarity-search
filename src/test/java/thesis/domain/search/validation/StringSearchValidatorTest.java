package thesis.domain.search.validation;

import org.junit.jupiter.api.Test;
import thesis.data.model.StringCategory;
import thesis.domain.search.dto.StringSearchOptions;
import thesis.domain.search.dto.enums.StringSearchType;
import thesis.exceptions.BadRequestException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringSearchValidatorTest {
    private final StringSearchValidator validator = new StringSearchValidator(new SearchFilterValidator());

    @Test
    void validateOptions_ShouldThrowException_WhenAllOptionsAreNull() {
        StringSearchOptions options = new StringSearchOptions();

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
        assertEquals("One of value, values or category must be set", exception.getMessage());
    }

    @Test
    void validateOptions_ShouldThrowException_WhenBothValueAndValuesAreSet() {
        StringSearchOptions options = new StringSearchOptions();
        options.setValue("testValue");
        options.setValues(List.of("value1", "value2"));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
        assertEquals("Only single value or multiple values can be set", exception.getMessage());
    }

    @Test
    void validateOptions_ShouldThrowException_WhenValuesAreEmpty() {
        StringSearchOptions options = new StringSearchOptions();
        options.setValues(List.of());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
        assertEquals("Values cannot be empty", exception.getMessage());
    }

    @Test
    void validateOptions_ShouldThrowException_WhenSearchTypeIsNotEqualAndMultipleValuesAreSet() {
        StringSearchOptions options = new StringSearchOptions();
        options.setValues(List.of("value1", "value2"));
        options.setSearchType(StringSearchType.GREATER_THAN);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
        assertEquals("Search type can only EQUAL when multiple values are set", exception.getMessage());
    }

    @Test
    void validateOptions_ShouldThrowException_WhenCategoryIsNotSetAndSearchTypeIsNotEqual() {
        StringSearchOptions options = new StringSearchOptions();
        options.setValue("testValue");
        options.setSearchType(StringSearchType.GREATER_THAN);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
        assertEquals("Search type can only be EQUAL when category is not set", exception.getMessage());
    }

    @Test
    void validateOptions_ShouldThrowException_WhenValueOrValuesAreNotSetAndCategoryIsSetWithNonEqualSearchType() {
        StringSearchOptions options = new StringSearchOptions();
        options.setCategoryName("testCategory");
        options.setSearchType(StringSearchType.LESS_THAN);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
        assertEquals("Search type can only be EQUAL when value or values are not set", exception.getMessage());
    }

    @Test
    void validateOptions_ShouldNotThrowException_WhenValidOptionsAreSet() {
        StringSearchOptions options = new StringSearchOptions();
        options.setValue("validValue");
        options.setSearchType(StringSearchType.EQUAL);

        assertDoesNotThrow(() -> validator.validateOptions(options));
    }

    @Test
    void validateCategory_ShouldThrowException_WhenValueNotInCategory() {
        StringCategory category = new StringCategory();
        category.setValues(List.of("value1", "value2"));

        StringSearchOptions options = new StringSearchOptions();
        options.setValue("value3");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateCategory(options, category));
        assertEquals("Value not found in category", exception.getMessage());
    }

    @Test
    void validateCategory_ShouldThrowException_WhenValuesNotInCategory() {
        StringCategory category = new StringCategory();
        category.setValues(List.of("value1", "value2"));

        StringSearchOptions options = new StringSearchOptions();
        options.setValues(List.of("value1", "value3"));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateCategory(options, category));
        assertEquals("Values not found in category", exception.getMessage());
    }

    @Test
    void validateCategory_ShouldThrowException_WhenCategoryIsNotComparableAndNonEqualSearchTypeIsSet() {
        StringCategory category = new StringCategory();
        category.setIsComparable(false);

        StringSearchOptions options = new StringSearchOptions();
        options.setSearchType(StringSearchType.LESS_THAN);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateCategory(options, category));
        assertEquals("Category is not comparable", exception.getMessage());
    }

    @Test
    void validateCategory_ShouldNotThrowException_WhenValueIsInCategory() {
        StringCategory category = new StringCategory();
        category.setIsComparable(false);
        category.setValues(List.of("value1", "value2"));

        StringSearchOptions options = new StringSearchOptions();
        options.setValue("value1");

        assertDoesNotThrow(() -> validator.validateCategory(options, category));
    }

    @Test
    void validateCategory_ShouldNotThrowException_WhenValuesAreInCategory() {
        StringCategory category = new StringCategory();
        category.setIsComparable(false);
        category.setValues(List.of("value1", "value2"));

        StringSearchOptions options = new StringSearchOptions();
        options.setValues(List.of("value1", "value2"));

        assertDoesNotThrow(() -> validator.validateCategory(options, category));
    }

    @Test
    void validateCategory_ShouldNotThrowException_WhenCategoryIsComparable() {
        StringCategory category = new StringCategory();
        category.setIsComparable(true);

        StringSearchOptions options = new StringSearchOptions();
        options.setSearchType(StringSearchType.LESS_THAN);

        assertDoesNotThrow(() -> validator.validateCategory(options, category));
    }
}

