package thesis.domain.search.service.helpers;

import thesis.data.model.Result;
import thesis.domain.search.dto.SearchFilters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for filtering results based on search filters.
 */
public final class ResultFilterUtil {
    private ResultFilterUtil() {
    }

    /**
     * Filters the results based on the provided search filters and technology names.
     *
     * @param results         the list of results to filter
     * @param filters         the search filters to apply
     * @param technologyNames the set of technology names to filter by
     * @return a list of filtered results
     */
    public static List<Result> filterResults(List<Result> results, SearchFilters filters, Set<String> technologyNames) {
        if (filters == null) {
            return results;
        }

        List<Result> filteredResults = new ArrayList<>();

        for (var result : results) {
            if (!technologyNames.isEmpty()) {
                if (result.getTechnologyName() == null || !technologyNames.contains(result.getTechnologyName())) {
                    continue;
                }
            }

            filterByCommonCriteria(filters, filteredResults, result);
        }

        return filteredResults;
    }

    /**
     * Filters the results based on the provided search filters.
     *
     * @param results the list of results to filter
     * @param filters the search filters to apply
     * @return a list of filtered results
     */
    public static List<Result> filterResults(List<Result> results, SearchFilters filters) {
        if (filters == null) {
            return results;
        }

        List<Result> filteredResults = new ArrayList<>();

        for (var result : results) {
            filterByCommonCriteria(filters, filteredResults, result);
        }

        return filteredResults;
    }

    private static void filterByCommonCriteria(SearchFilters filters, List<Result> filteredResults, Result result) {
        if (filters.getSample() != null) {
            if (result.getSample() == null || !result.getSample().equals(filters.getSample())) {
                return;
            }
        }

        if (filters.getFromTimestamp() != null) {
            if (result.getTimestamp() == null || result.getTimestamp().isBefore(filters.getFromTimestamp())) {
                return;
            }
        }

        if (filters.getToTimestamp() != null) {
            if (result.getTimestamp() == null || result.getTimestamp().isAfter(filters.getToTimestamp())) {
                return;
            }
        }

        filteredResults.add(result);
    }
}
