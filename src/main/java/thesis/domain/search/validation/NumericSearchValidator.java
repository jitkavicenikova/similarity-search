package thesis.domain.search.validation;

import org.springframework.stereotype.Component;
import thesis.domain.search.dto.NumericSearchOptions;
import thesis.domain.search.dto.enums.NumericSearchType;
import thesis.exceptions.BadRequestException;

/**
 * Validator class for boolean search options.
 * It checks the validity of the provided options and ensures that they conform to the expected format and constraints.
 */
@Component
public class NumericSearchValidator {
    private final SearchFilterValidator searchFilterValidator;

    public NumericSearchValidator(SearchFilterValidator searchFilterValidator) {
        this.searchFilterValidator = searchFilterValidator;
    }

    /**
     * Validates the provided numeric search options.
     *
     * @param options the numeric search options to validate
     * @throws BadRequestException if the options are invalid
     */
    public void validateOptions(NumericSearchOptions options) {
        searchFilterValidator.validate(options.getFilters());
        if (options.getMinimum() == null && options.getMaximum() == null && options.getValue() == null) {
            throw new BadRequestException("At least one of minimum or maximum must be provided");
        }

        if (options.getValue() != null && (options.getMinimum() != null || options.getMaximum() != null)) {
            throw new BadRequestException("Value cannot be provided with minimum or maximum");
        }

        if (options.getMinimum() != null && options.getMaximum() != null
                && options.getMinimum() > options.getMaximum()) {
            throw new BadRequestException("Minimum must be less than maximum");
        }

        if (options.getAbsoluteDeviation() != null && options.getPercentageDeviation() != null) {
            throw new BadRequestException("Only one of absolute or percentage deviation can be provided");
        }

        if (options.getAbsoluteTolerance() != null && options.getPercentageTolerance() != null) {
            throw new BadRequestException("Only one of absolute or percentage tolerance can be provided");
        }

        if (options.getValue() != null && options.getPercentageDeviation() == null && options.getAbsoluteDeviation() == null
                && options.getSearchType() != null && options.getSearchType() != NumericSearchType.EXACT_MATCH) {
            throw new BadRequestException("Search type can only be set to exact match when value is provided without deviation");
        }

        if (options.getMinimum() != null && options.getMaximum() == null && options.getSearchType() != null && (
                options.getSearchType() == NumericSearchType.MAX_OUT_OF_RANGE || options.getSearchType() == NumericSearchType.BOTH_OUT_OF_RANGE)) {
            throw new BadRequestException("Search type can't be set to max out of range or both out of range without minimum");
        }

        if (options.getMaximum() != null && options.getMinimum() == null && options.getSearchType() != null && (
                options.getSearchType() == NumericSearchType.MIN_OUT_OF_RANGE || options.getSearchType() == NumericSearchType.BOTH_OUT_OF_RANGE)) {
            throw new BadRequestException("Search type can't be set to min out of range or both out of range without maximum");
        }

        if ((options.getPercentageTolerance() != null || options.getAbsoluteTolerance() != null) &&
                (options.getSearchType() == null || options.getSearchType() == NumericSearchType.EXACT_MATCH)) {
            throw new BadRequestException("Tolerance can't be set when searching for exact match");
        }

        if (options.getFilters() != null && options.getFilters().getTechnologyName() == null && options.getFilters().getIncludeComparableTechnologies() != null) {
            throw new BadRequestException("Technology name must be provided when including comparable technologies");
        }

        if (options.getFilters() != null && options.getFilters().getFromTimestamp() != null && options.getFilters().getToTimestamp() != null
                && options.getFilters().getFromTimestamp().isAfter(options.getFilters().getToTimestamp())) {
            throw new BadRequestException("From timestamp must be before to timestamp");
        }
    }
}
