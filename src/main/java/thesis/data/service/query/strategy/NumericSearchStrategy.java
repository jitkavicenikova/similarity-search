package thesis.data.service.query.strategy;

import java.util.Set;

/**
 * Interface for numeric search strategies.
 * <p>
 * This interface defines methods for searching results based on numeric values.
 * Implementations of this interface should provide specific search algorithms.
 * </p>
 */
public interface NumericSearchStrategy {
    /**
     * Searches for results based on the given marker name, minimum and maximum values.
     *
     * @param markerName            the name of the marker
     * @param min                   the minimum value
     * @param max                   the maximum value
     * @param useTechnologyDeviations whether to include technology deviations
     * @return a set of record IDs that match the criteria
     */
    Set<String> search(String markerName, Double min, Double max, Boolean useTechnologyDeviations);

    /**
     * Searches for results based on the given marker name, minimum and maximum values with tolerance.
     *
     * @param markerName            the name of the marker
     * @param min                   the minimum value
     * @param max                   the maximum value
     * @param minWithTolerance      the minimum value with tolerance
     * @param maxWithTolerance      the maximum value with tolerance
     * @param useTechnologyDeviations whether to include technology deviations
     * @return a set of record IDs that match the criteria
     */
    Set<String> searchWithTolerance(String markerName, Double min, Double max, Double minWithTolerance, Double maxWithTolerance, Boolean useTechnologyDeviations);
}
