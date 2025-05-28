package thesis.data.service.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Result;
import thesis.data.repository.ResultRepository;
import thesis.data.service.query.strategy.*;
import thesis.domain.search.dto.RecursiveResult;
import thesis.domain.search.dto.enums.NumericSearchType;
import thesis.exceptions.EntityNotFoundException;
import thesis.utils.EntityUtils;

import java.util.*;

/**
 * Service class for querying results based on numeric values.
 */
@Service
public class ResultNumericQueryService {
    private final Map<NumericSearchType, NumericSearchStrategy> searchStrategies;
    private final ResultRepository resultRepository;

    @Autowired
    public ResultNumericQueryService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
        this.searchStrategies = Map.of(
                NumericSearchType.MIN_OUT_OF_RANGE, new MinOutOfRangeSearchStrategy(resultRepository),
                NumericSearchType.MAX_OUT_OF_RANGE, new MaxOutOfRangeSearchStrategy(resultRepository),
                NumericSearchType.BOTH_OUT_OF_RANGE, new BothOutOfRangeSearchStrategy(resultRepository),
                NumericSearchType.EXACT_MATCH, new ExactMatchSearchStrategy(resultRepository)
        );
    }

    /**
     * Retrieves a list of results based on the provided record IDs and marker name.
     *
     * @param recordIds  A set of record IDs to search for.
     * @param markerName The name of the marker associated with the results.
     * @return A list of `Result` objects matching the provided record IDs and marker name.
     * @throws EntityNotFoundException If a result with a specific ID is not found.
     */
    public List<Result> getResultsByIds(Set<String> recordIds, String markerName) {
        return recordIds.stream()
                .map(id -> resultRepository.findById(EntityUtils.generateResultId(id, markerName))
                        .orElseThrow(() -> new EntityNotFoundException("Result with id " + id + " not found")))
                .toList();
    }

    /**
     * Searches for results based on the provided marker name, minimum and maximum values,
     * search type, and technology deviations.
     *
     * @param markerName            The name of the marker.
     * @param min                   The minimum value.
     * @param max                   The maximum value.
     * @param searchType            The type of numeric search to perform.
     * @param useTechnologyDeviations Whether to include technology deviations in the search.
     * @return A set of record IDs that match the search criteria.
     */
    public Set<String> searchResults(String markerName, Double min, Double max,
                                     NumericSearchType searchType, Boolean useTechnologyDeviations) {
        if (min == null && searchType == NumericSearchType.EXACT_MATCH) {
            return resultRepository.searchForMaximumMatches(markerName, Double.NEGATIVE_INFINITY, max, useTechnologyDeviations);
        }

        if (max == null && searchType == NumericSearchType.EXACT_MATCH) {
            return resultRepository.searchForMinimumMatches(markerName, min, Double.POSITIVE_INFINITY, useTechnologyDeviations);
        }

        NumericSearchStrategy strategy = searchStrategies.get(searchType);

        return strategy.search(markerName, min, max, useTechnologyDeviations);
    }


    /**
     * Searches for results based on the provided marker name, minimum and maximum values,
     * tolerance values, search type, and technology deviations.
     *
     * @param markerName            The name of the marker.
     * @param min                   The minimum value.
     * @param max                   The maximum value.
     * @param minWithTolerance      The minimum value with tolerance applied.
     * @param maxWithTolerance      The maximum value with tolerance applied.
     * @param searchType            The type of numeric search to perform.
     * @param useTechnologyDeviations Whether to include technology deviations in the search.
     * @return A set of record IDs that match the search criteria with tolerance applied.
     */
    public Set<String> searchResultsWithTolerance(String markerName, Double min, Double max,
                                                  Double minWithTolerance, Double maxWithTolerance,
                                                  NumericSearchType searchType, Boolean useTechnologyDeviations) {
        NumericSearchStrategy strategy = searchStrategies.get(searchType);

        return strategy.searchWithTolerance(markerName, min, max, minWithTolerance, maxWithTolerance, useTechnologyDeviations);
    }

    /**
     * Retrieves all results for a specific marker name, including both minimum and maximum values.
     *
     * @param markerName            The name of the marker.
     * @param useTechnologyDeviations Whether to include technology deviations in the search.
     * @return A list of RecursiveResult objects containing the results for the specified marker.
     */
    public List<RecursiveResult> getAllResultsForMarker(String markerName, Boolean useTechnologyDeviations) {
        var minResults = resultRepository.getAllMinResultsForMarker(markerName, useTechnologyDeviations);
        var maxResults = resultRepository.getAllMaxResultsForMarker(markerName, useTechnologyDeviations);

        Map<String, RecursiveResult> resultMap = new HashMap<>();
        if (minResults != null) {
            minResults.forEach(tuple ->
                    resultMap.put(tuple.getValue(), new RecursiveResult(tuple.getValue(), tuple.getScore(), null))
            );
        }

        if (maxResults != null) {
            maxResults.forEach(tuple ->
                    resultMap.computeIfAbsent(tuple.getValue(),
                                    k -> new RecursiveResult(tuple.getValue(), null, tuple.getScore()))
                            .setMax(tuple.getScore())
            );
        }
        return new ArrayList<>(resultMap.values());
    }
}
