package thesis.domain.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Marker;
import thesis.data.model.Unit;
import thesis.data.service.MarkerService;
import thesis.data.service.query.ResultNumericQueryService;
import thesis.domain.search.dto.AggregatedResult;
import thesis.domain.search.dto.NumericSearchConfiguration;
import thesis.domain.search.dto.RecursiveResult;
import thesis.domain.search.service.helpers.RecursiveNumericSearchUtil;
import thesis.domain.search.service.helpers.SearchConversionService;
import thesis.utils.EntityUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for performing recursive numeric searches.
 * It retrieves results for a given marker and its child markers, aggregates them, and filters them based on the provided configuration.
 */
@Service
public class RecursiveNumericSearchService {
    private final SearchConversionService searchConversionService;
    private final ResultNumericQueryService resultService;
    private final MarkerService markerService;

    @Autowired
    public RecursiveNumericSearchService(SearchConversionService searchConversionService, ResultNumericQueryService resultService, MarkerService markerService) {
        this.searchConversionService = searchConversionService;
        this.resultService = resultService;
        this.markerService = markerService;
    }

    /**
     * Retrieves and aggregates results for a given marker and its child markers.
     *
     * @param config the numeric search configuration
     * @param marker the marker for which to retrieve results
     * @param unit   the unit to which the results should be converted
     * @return a list of aggregated results
     */
    public List<AggregatedResult> getResultsForMarkerWithChildren(NumericSearchConfiguration config, Marker marker, Unit unit) {
        var results = getResultsRecursive(marker, unit, config.getUseTechnologyDeviation());

        Map<String, List<RecursiveResult>> groupedResults = results.stream()
                .collect(Collectors.groupingBy(RecursiveResult::getRecordId));

        return groupedResults.entrySet().stream()
                .map(entry -> RecursiveNumericSearchUtil.aggregateResults(entry.getKey(), entry.getValue(), marker.getAggregationType()))
                .filter(aggregatedResult -> RecursiveNumericSearchUtil.filterByNumericSearchType(config, aggregatedResult.getMin(), aggregatedResult.getMax()))
                .toList();
    }

    private List<RecursiveResult> getResultsRecursive(Marker marker, Unit unit, Boolean useTechnologyDeviations) {
        if (marker.getChildMarkerNames() == null) {
            return getMarkerResults(marker, unit, useTechnologyDeviations);
        }

        return marker.getChildMarkerNames().stream()
                .map(markerService::getEntity)
                .flatMap(childMarker -> getResultsRecursive(childMarker, unit, useTechnologyDeviations).stream())
                .collect(Collectors.toList());

    }

    private List<RecursiveResult> getMarkerResults(Marker childMarker, Unit unit, Boolean useTechnologyDeviations) {
        var results = Optional.ofNullable(resultService.getAllResultsForMarker(childMarker.getName(), useTechnologyDeviations))
                .orElse(Collections.emptyList());

        results.forEach(result -> {
            result.setMarkerName(childMarker.getName());
            result.setResultId(EntityUtils.generateResultId(result.getRecordId(), childMarker.getName()));
        });

        return searchConversionService.convertRecursiveResults(childMarker, unit, results);
    }
}
