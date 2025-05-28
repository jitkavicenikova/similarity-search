package thesis.domain.search.validation;

import org.springframework.stereotype.Component;
import thesis.data.model.StringCategory;
import thesis.domain.search.dto.StringSearchOptions;
import thesis.domain.search.dto.enums.StringSearchType;
import thesis.exceptions.BadRequestException;

/**
 * Validator class for string search options.
 * It checks the validity of the provided options and ensures that they conform to the expected format and constraints.
 */
@Component
public class StringSearchValidator {
    private final SearchFilterValidator searchFilterValidator;

    public StringSearchValidator(SearchFilterValidator searchFilterValidator) {
        this.searchFilterValidator = searchFilterValidator;
    }

    /**
     * Validates the provided string search options.
     *
     * @param options the string search options to validate
     * @throws BadRequestException if the options are invalid
     */
    public void validateOptions(StringSearchOptions options) {
        searchFilterValidator.validate(options.getFilters());

        if (options.getValue() == null && options.getValues() == null && options.getCategoryName() == null) {
            throw new BadRequestException("One of value, values or category must be set");
        }

        if (options.getValue() != null && options.getValues() != null) {
            throw new BadRequestException("Only single value or multiple values can be set");
        }

        if (options.getValues() != null && options.getValues().isEmpty()) {
            throw new BadRequestException("Values cannot be empty");
        }

        if (options.getValues() != null && options.getSearchType() != null
                && !options.getSearchType().equals(StringSearchType.EQUAL)) {
            throw new BadRequestException("Search type can only EQUAL when multiple values are set");
        }

        if (options.getCategoryName() == null && options.getSearchType() != null
                && !options.getSearchType().equals(StringSearchType.EQUAL)) {
            throw new BadRequestException("Search type can only be EQUAL when category is not set");
        }

        if (options.getValue() == null && options.getValues() == null && options.getCategoryName() != null
                && options.getSearchType() != null && !options.getSearchType().equals(StringSearchType.EQUAL)) {
            throw new BadRequestException("Search type can only be EQUAL when value or values are not set");
        }
    }

    public void validateCategory(StringSearchOptions options, StringCategory category) {
        if (options.getValue() != null && !category.getValues().contains(options.getValue())) {
            throw new BadRequestException("Value not found in category");
        }

        if (options.getValues() != null && !category.getValues().containsAll(options.getValues())) {
            throw new BadRequestException("Values not found in category");
        }

        if (!category.getIsComparable() && options.getSearchType() != null
                && !options.getSearchType().equals(StringSearchType.EQUAL)) {
            throw new BadRequestException("Category is not comparable");
        }
    }
}
