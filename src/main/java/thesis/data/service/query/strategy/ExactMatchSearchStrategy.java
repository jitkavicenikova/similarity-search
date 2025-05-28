package thesis.data.service.query.strategy;

import thesis.data.repository.ResultRepository;
import thesis.exceptions.BadRequestException;
import thesis.utils.SetUtils;

import java.util.Set;

/**
 * This class implements the NumericSearchStrategy interface for performing exact match searches.
 * <p>
 * It uses the ResultRepository to search for results that match the given numeric range exactly.
 * </p>
 */
public class ExactMatchSearchStrategy implements NumericSearchStrategy {
    private final ResultRepository resultRepository;

    public ExactMatchSearchStrategy(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public Set<String> search(String markerName, Double min, Double max, Boolean useTechnologyDeviations) {
        var minimumMatches = resultRepository.searchForMinimumMatches(markerName, min, max, useTechnologyDeviations);
        var maximumMatches = resultRepository.searchForMaximumMatches(markerName, min, max, useTechnologyDeviations);

        return SetUtils.getIntersection(minimumMatches, maximumMatches);
    }

    @Override
    public Set<String> searchWithTolerance(String markerName, Double min, Double max, Double minWithTolerance, Double maxWithTolerance, Boolean useTechnologyDeviations) {
        throw new BadRequestException("Exact match search is not supported for numeric search with tolerance");
    }
}
