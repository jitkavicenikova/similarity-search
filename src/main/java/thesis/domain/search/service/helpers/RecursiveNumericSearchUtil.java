package thesis.domain.search.service.helpers;

import thesis.data.enums.AggregationType;
import thesis.domain.search.dto.AggregatedResult;
import thesis.domain.search.dto.NumericSearchConfiguration;
import thesis.domain.search.dto.RecursiveResult;
import thesis.exceptions.BadRequestException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for handling recursive numeric search operations.
 */
public final class RecursiveNumericSearchUtil {
    private RecursiveNumericSearchUtil() {
    }

    /**
     * Filters the numeric search results based on the provided configuration and min/max values.
     *
     * @param config the numeric search configuration
     * @param min    the minimum value
     * @param max    the maximum value
     * @return true if the result is within the specified range, false otherwise
     */
    public static boolean filterByNumericSearchType(NumericSearchConfiguration config, Double min, Double max) {
        min = min == null ? Double.NEGATIVE_INFINITY : min;
        max = max == null ? Double.POSITIVE_INFINITY : max;

        // handle cases where conversion failed
        if (Double.isNaN(min) || Double.isNaN(max)) {
            return false;
        }

        return config.getWithTolerance()
                ? filterWithTolerance(config, min, max)
                : filterWithoutTolerance(config, min, max);
    }

    /**
     * Aggregates the results of a recursive search based on the specified aggregation type.
     *
     * @param recordId      the ID of the record
     * @param recordResults the list of recursive results
     * @param aggregationType the type of aggregation to perform
     * @return an AggregatedResult object containing the aggregated results
     */
    public static AggregatedResult aggregateResults(String recordId, List<RecursiveResult> recordResults, AggregationType aggregationType) {
        double sumMin = recordResults.stream()
                .mapToDouble(result -> result.getMin() != null ? result.getMin() : Double.NEGATIVE_INFINITY)
                .sum();
        double sumMax = recordResults.stream()
                .mapToDouble(result -> result.getMax() != null ? result.getMax() : Double.POSITIVE_INFINITY)
                .sum();
        int count = recordResults.size();

        var res = new AggregatedResult(recordId, sumMin, sumMax,
                recordResults.stream().map(RecursiveResult::getMarkerName).collect(Collectors.toList()),
                recordResults.stream().map(RecursiveResult::getResultId).collect(Collectors.toList()));

        if (aggregationType == AggregationType.AVERAGE) {
            res.setMin(sumMin / count);
            res.setMax(sumMax / count);
        }

        return res;
    }

    private static boolean filterWithoutTolerance(NumericSearchConfiguration config, Double min, Double max) {
        return switch (config.getSearchType()) {
            case EXACT_MATCH -> config.getMinimum() <= min && config.getMaximum() >= max;
            case MIN_OUT_OF_RANGE -> config.getMaximum() >= max && config.getMinimum() <= max &&
                    min < config.getMinimum();
            case MAX_OUT_OF_RANGE -> config.getMaximum() >= min && config.getMinimum() <= min &&
                    max > config.getMaximum();
            case BOTH_OUT_OF_RANGE -> min < config.getMinimum() && max > config.getMaximum();
        };
    }

    private static boolean filterWithTolerance(NumericSearchConfiguration config, Double min, Double max) {
        return switch (config.getSearchType()) {
            case EXACT_MATCH ->
                    throw new BadRequestException("Exact match search is not supported for numeric search with tolerance");
            case MIN_OUT_OF_RANGE -> max >= config.getMinimum() && max <= config.getMaximum() &&
                    min >= config.getMinimumWithTolerance() && min < config.getMinimum();
            case MAX_OUT_OF_RANGE -> min >= config.getMinimum() && min <= config.getMaximum() &&
                    max <= config.getMaximumWithTolerance() && max > config.getMaximum();
            case BOTH_OUT_OF_RANGE -> min >= config.getMinimumWithTolerance() && min < config.getMinimum() &&
                    max <= config.getMaximumWithTolerance() && max > config.getMaximum();
        };
    }
}
