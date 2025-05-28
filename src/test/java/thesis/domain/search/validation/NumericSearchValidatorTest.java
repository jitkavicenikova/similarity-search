package thesis.domain.search.validation;

import org.junit.jupiter.api.Test;
import thesis.domain.search.dto.NumericSearchOptions;
import thesis.domain.search.dto.enums.NumericSearchType;
import thesis.exceptions.BadRequestException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumericSearchValidatorTest {
    private final NumericSearchValidator validator = new NumericSearchValidator(new SearchFilterValidator());

    @Test
    void validateOptions_ShouldThrowException_WhenNoMinimumMaximumOrValueProvided() {
        NumericSearchOptions options = new NumericSearchOptions();
        assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldThrowException_WhenValueAndMinOrMaxAreProvided() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setValue(10.0);
        options.setMinimum(5.0);
        assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldThrowException_WhenBothAbsoluteAndPercentageDeviationAreProvided() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setAbsoluteDeviation(1.0);
        options.setPercentageDeviation(10.0);
        assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldThrowException_WhenBothAbsoluteAndPercentageToleranceAreProvided() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setAbsoluteTolerance(1.0);
        options.setPercentageTolerance(5.0);
        assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldThrowException_WhenValueIsProvidedWithoutDeviationAndSearchTypeIsNotExactMatch() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setValue(15.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);
        assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldThrowException_WhenToleranceIsSetWithExactMatchSearchType() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setValue(10.0);
        options.setAbsoluteTolerance(0.5);
        options.setSearchType(NumericSearchType.EXACT_MATCH);
        assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldPass_WhenValidExactMatchSearchOptionsAreProvided() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setValue(10.0);
        options.setSearchType(NumericSearchType.EXACT_MATCH);
        assertDoesNotThrow(() -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldPass_WhenValidRangeWithMinimumAndMaximumIsProvided() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(5.0);
        options.setMaximum(15.0);
        assertDoesNotThrow(() -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldPass_WhenOnlyMinimumOrMaximumIsProvidedWithoutConflictingSearchType() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(5.0);
        assertDoesNotThrow(() -> validator.validateOptions(options));

        options.setMaximum(15.0);
        assertDoesNotThrow(() -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldThrowException_WhenSearchTypeBothOutOfRangeIsSetWithoutMaximum() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(10.0);
        options.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);

        assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
    }

    @Test
    void validateOptions_ShouldThrowException_WhenSearchTypeMinOutOfRangeOrBothOutOfRangeIsSetWithoutMinimum() {
        NumericSearchOptions options1 = new NumericSearchOptions();
        options1.setMaximum(20.0);
        options1.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);
        assertThrows(BadRequestException.class, () -> validator.validateOptions(options1));

        NumericSearchOptions options2 = new NumericSearchOptions();
        options2.setMaximum(20.0);
        options2.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);
        assertThrows(BadRequestException.class, () -> validator.validateOptions(options2));
    }

    @Test
    void validateOptions_ShouldPass_WhenSearchTypeMaxOutOfRangeIsSetWithOnlyMaximum() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMaximum(20.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        validator.validateOptions(options);
    }

    @Test
    void validateOptions_ShouldPass_WhenSearchTypeMinOutOfRangeIsSetWithOnlyMinimum() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(10.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        validator.validateOptions(options);
    }

    @Test
    void validateOptions_ShouldThrowException_WhenSearchTypeBothOutOfRangeIsSetWithoutMinimum() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMaximum(20.0);
        options.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);
        assertThrows(BadRequestException.class, () -> validator.validateOptions(options));
    }
}
