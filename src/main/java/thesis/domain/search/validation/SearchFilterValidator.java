package thesis.domain.search.validation;

import org.springframework.stereotype.Component;
import thesis.domain.search.dto.SearchFilters;
import thesis.exceptions.BadRequestException;

/**
 * Validator class for search filters.
 * It checks the validity of the provided filters and ensures that they conform to the expected format and constraints.
 */
@Component
public class SearchFilterValidator {
    /**
     * Validates the provided search filters.
     *
     * @param filters the search filters to validate
     * @throws BadRequestException if the filters are invalid
     */
    public void validate(SearchFilters filters) {
        if (filters == null) {
            return;
        }

        if (filters.getTechnologyName() == null && filters.getIncludeComparableTechnologies() != null) {
            throw new BadRequestException("Technology name must be provided when including comparable technologies");
        }

        if (filters.getFromTimestamp() != null && filters.getToTimestamp() != null
                && filters.getFromTimestamp().isAfter(filters.getToTimestamp())) {
            throw new BadRequestException("From timestamp must be before to timestamp");
        }
    }
}
