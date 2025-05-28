package thesis.domain.search.validation;

import org.junit.jupiter.api.Test;
import thesis.domain.search.dto.BoolSearchOptions;
import thesis.domain.search.dto.SearchFilters;
import thesis.exceptions.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;

class BoolSearchValidatorTest {
    private final BoolSearchValidator validator = new BoolSearchValidator(new SearchFilterValidator());

    @Test
    void validateOptions_ShouldNotThrowException_WhenOnlyTechnologyNameIsSet() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setFilters(new SearchFilters("techA", null, null, null, null));

        assertDoesNotThrow(() -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldNotThrowException_WhenOnlySpecificityIsSet() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMinSpecificity(0.8);

        assertDoesNotThrow(() -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldNotThrowException_WhenOnlySensitivityIsSet() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMinSensitivity(0.7);

        assertDoesNotThrow(() -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldNotThrowException_WhenBothSpecificityAndSensitivityAreSet() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMinSpecificity(0.8);
        options.setMinSensitivity(0.7);

        assertDoesNotThrow(() -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldThrowException_WhenTechnologyAndSpecificityAreSet() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setFilters(new SearchFilters("techA", null, null, null, null));
        options.setMinSpecificity(0.8);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
        assertEquals("Technology name cannot not be set when searching by specificity or sensitivity", exception.getMessage());
    }

    @Test
    void validateOptions_ShouldThrowException_WhenTechnologyAndSensitivityAreSet() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setFilters(new SearchFilters("techA", null, null, null, null));
        options.setMinSensitivity(0.7);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
        assertEquals("Technology name cannot not be set when searching by specificity or sensitivity", exception.getMessage());
    }

    @Test
    void validateOptions_ShouldThrowException_WhenTechnologySpecificityAndSensitivityAreSet() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setFilters(new SearchFilters("techC", null, null, null, null));
        options.setMinSpecificity(0.8);
        options.setMinSensitivity(0.7);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
        assertEquals("Technology name cannot not be set when searching by specificity or sensitivity", exception.getMessage());
    }

    @Test
    void validateOptions_ShouldThrowException_WhenComparableSpecificityAndSensitivityAreSet() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setFilters(new SearchFilters(null, true, null, null, null));

        options.setMinSpecificity(0.8);
        options.setMinSensitivity(0.7);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
        assertEquals("Include comparable technologies cannot be set when searching by specificity or sensitivity", exception.getMessage());
    }
}
