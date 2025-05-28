package thesis.domain.search.validation;

import org.springframework.stereotype.Component;
import thesis.domain.search.dto.BoolSearchOptions;
import thesis.exceptions.BadRequestException;

/**
 * Validator class for boolean search options.
 * It checks the validity of the provided options and ensures that they conform to the expected format and constraints.
 */
@Component
public class BoolSearchValidator {
    private final SearchFilterValidator searchFilterValidator;

    public BoolSearchValidator(SearchFilterValidator searchFilterValidator) {
        this.searchFilterValidator = searchFilterValidator;
    }

    /**
     * Validates the provided boolean search options.
     *
     * @param options the boolean search options to validate
     * @throws BadRequestException if the options are invalid
     */
    public void validateOptions(BoolSearchOptions options) {
        var thresholdsSet = options.getMinSpecificity() != null || options.getMinSensitivity() != null;
        if (thresholdsSet && options.getFilters() != null) {
            if (options.getFilters().getTechnologyName() != null) {
                throw new BadRequestException("Technology name cannot not be set when searching by specificity or sensitivity");
            }

            if (options.getFilters().getIncludeComparableTechnologies() != null) {
                throw new BadRequestException("Include comparable technologies cannot be set when searching by specificity or sensitivity");
            }
        }

        searchFilterValidator.validate(options.getFilters());
    }
}
