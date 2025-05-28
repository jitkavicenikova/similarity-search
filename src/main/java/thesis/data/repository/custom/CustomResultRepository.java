package thesis.data.repository.custom;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import thesis.data.model.Result;

import java.util.Set;

/**
 * Custom repository interface for Result entity.
 * This interface defines methods for saving, deleting, and searching results in a Redis database.
 */
public interface CustomResultRepository {

    /**
     * Saves min and max of the result to indexes for search.
     *
     * @param result the result to save
     */
    void saveResultSearchIndex(Result result);

    /**
     * Saves min and max of the result with applied technology deviations to indexes for search.
     *
     * @param result the result to save
     * @param min    the minimum value with applied technology deviation
     * @param max    the maximum value with applied technology deviation
     */
    void saveResultSearchIndexWithTechDeviations(Result result, Double min, Double max);

    /**
     * Searches for results with minimum matches for a given marker name and range.
     *
     * @param markerName            the name of the marker
     * @param min                   the minimum value
     * @param max                   the maximum value
     * @param withTechDeviations    whether to include technology deviations
     * @return a set of record IDs that match the criteria
     */
    Set<String> searchForMinimumMatches(String markerName, Double min, Double max, Boolean withTechDeviations);

    /**
     * Searches for results with maximum matches for a given marker name and range.
     *
     * @param markerName            the name of the marker
     * @param min                   the minimum value
     * @param max                   the maximum value
     * @param withTechDeviations    whether to include technology deviations
     * @return a set of record IDs that match the criteria
     */
    Set<String> searchForMaximumMatches(String markerName, Double min, Double max, Boolean withTechDeviations);

    /**
     * Retrieves all minimum results for a given marker name.
     *
     * @param markerName            the name of the marker
     * @param withTechDeviations    whether to include technology deviations
     * @return a set of typed tuples containing result IDs and their scores
     */
    Set<TypedTuple<String>> getAllMinResultsForMarker(String markerName, Boolean withTechDeviations);

    /**
     * Retrieves all maximum results for a given marker name.
     *
     * @param markerName            the name of the marker
     * @param withTechDeviations    whether to include technology deviations
     * @return a set of typed tuples containing result IDs and their scores
     */
    Set<TypedTuple<String>> getAllMaxResultsForMarker(String markerName, Boolean withTechDeviations);

    /**
     * Retrieves the technology name for a given record ID and marker name.
     *
     * @param recordId   the ID of the record
     * @param markerName  the name of the marker
     * @return the technology name as a String
     */
    String getTechnologyName(String recordId, String markerName);

    /**
     * Deletes the search index for a given result.
     *
     * @param result the result to delete
     */
    void deleteResultSearchIndex(Result result);

    /**
     * Deletes the search index with applied technology deviations for a given result.
     *
     * @param result the result to delete
     */
    void deleteResultSearchIndexWithTechDeviations(Result result);
}
