package thesis.domain.search.service.helpers;

import org.springframework.stereotype.Component;
import thesis.domain.search.dto.NumericSearchConfiguration;
import thesis.domain.search.dto.NumericSearchOptions;
import thesis.domain.search.dto.enums.NumericSearchType;

import java.util.Optional;

/**
 * This class is responsible for building the NumericSearchConfiguration object based on the provided NumericSearchOptions.
 * It applies the necessary calculations for minimum and maximum values, including deviations and tolerances.
 */
@Component
public class NumericSearchConfigurationBuilder {
    /**
     * Builds a NumericSearchConfiguration object based on the provided NumericSearchOptions.
     *
     * @param options the NumericSearchOptions containing the search parameters
     * @return a NumericSearchConfiguration object with the calculated values
     */
    public NumericSearchConfiguration getSearchConfiguration(NumericSearchOptions options) {
        var config = new NumericSearchConfiguration();

        var absoluteDev = getOrDefault(options.getAbsoluteDeviation());
        var percentageDev = getOrDefault(options.getPercentageDeviation());
        var absoluteTolerance = getOrDefault(options.getAbsoluteTolerance());
        var percentageTolerance = getOrDefault(options.getPercentageTolerance());

        setValues(config, options, absoluteDev, percentageDev, absoluteTolerance, percentageTolerance);

        config.setWithTolerance(absoluteTolerance != 0.0 || percentageTolerance != 0.0);
        // if the searchType is null, set it to exact match
        config.setSearchType(Optional.ofNullable(options.getSearchType()).orElse(NumericSearchType.EXACT_MATCH));
        config.setMarkerName(options.getMarkerName());
        // set useTechnologyDeviation to false if not provided
        config.setUseTechnologyDeviation(Optional.ofNullable(options.getUseTechnologyDeviation()).orElse(false));

        return config;
    }

    private void setValues(NumericSearchConfiguration config, NumericSearchOptions options, double absoluteDev, double percentageDev,
                           double absoluteTolerance, double percentageTolerance) {
        var min = options.getValue() != null ? options.getValue() : options.getMinimum();
        var max = options.getValue() != null ? options.getValue() : options.getMaximum();

        if (min != null) {
            var minWithDeviation = applyNegativeOffset(min, absoluteDev, percentageDev);
            config.setMinimum(minWithDeviation);
            config.setMinimumWithTolerance(applyNegativeOffset(minWithDeviation, absoluteTolerance, percentageTolerance));
        } else {
            config.setMinimum(Double.NEGATIVE_INFINITY);
        }

        if (max != null) {
            var maxWithDeviation = applyPositiveOffset(max, absoluteDev, percentageDev);
            config.setMaximum(maxWithDeviation);
            config.setMaximumWithTolerance(applyPositiveOffset(maxWithDeviation, absoluteTolerance, percentageTolerance));
        } else {
            config.setMaximum(Double.POSITIVE_INFINITY);
        }
    }

    private Double applyNegativeOffset(Double value, Double absoluteOffset, Double percentageOffset) {
        return value - absoluteOffset - (value * percentageOffset / 100);
    }

    private Double applyPositiveOffset(Double value, Double absoluteOffset, Double percentageOffset) {
        return value + absoluteOffset + (value * percentageOffset / 100);
    }

    private double getOrDefault(Double value) {
        return value != null ? value : 0.0;
    }
}
