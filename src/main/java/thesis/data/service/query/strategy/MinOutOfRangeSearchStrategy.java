package thesis.data.service.query.strategy;

import thesis.data.repository.ResultRepository;
import thesis.utils.SetUtils;

import java.util.Set;

/**
 * This class implements the NumericSearchStrategy interface for searching results
 * when the minimum value is out of range.
 * <p>
 * It uses the ResultRepository to perform the search operations.
 * </p>
 */
public class MinOutOfRangeSearchStrategy implements NumericSearchStrategy {
    private final ResultRepository resultRepository;

    public MinOutOfRangeSearchStrategy(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public Set<String> search(String markerName, Double min, Double max, Boolean useTechnologyDeviations) {
        var minMatches = resultRepository.searchForMinimumMatches(markerName, Double.NEGATIVE_INFINITY, min - Math.ulp(min), useTechnologyDeviations);
        var maxMatches = resultRepository.searchForMaximumMatches(markerName, min, max, useTechnologyDeviations);

        return SetUtils.getIntersection(minMatches, maxMatches);
    }

    @Override
    public Set<String> searchWithTolerance(String markerName, Double min, Double max, Double minWithTolerance, Double maxWithTolerance, Boolean useTechnologyDeviations) {
        var minimumMatches = resultRepository.searchForMinimumMatches(markerName, minWithTolerance, min - Math.ulp(min), useTechnologyDeviations);
        var maximumMatches = resultRepository.searchForMaximumMatches(markerName, min, max, useTechnologyDeviations);

        return SetUtils.getIntersection(maximumMatches, minimumMatches);
    }
}
