package thesis.domain.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Marker;
import thesis.data.model.Unit;
import thesis.data.service.MarkerService;
import thesis.data.service.UnitService;
import thesis.data.service.query.ResultNumericQueryService;
import thesis.domain.search.dto.NumericSearchConfiguration;
import thesis.domain.search.dto.NumericSearchOptions;
import thesis.domain.search.dto.NumericSearchResult;
import thesis.domain.search.service.helpers.NumericSearchConfigurationBuilder;
import thesis.domain.search.service.helpers.ResultFilterUtil;
import thesis.domain.search.service.helpers.SearchConversionService;
import thesis.domain.search.service.helpers.TechnologyResolver;
import thesis.domain.search.validation.NumericSearchValidator;
import thesis.exceptions.BadRequestException;

import java.util.Objects;
import java.util.Set;

/**
 * Service class for processing numeric search options.
 * It interacts with the ResultNumericQueryService and MarkerService to fetch and filter results based on the provided options.
 */
@Service
public class NumericSearchService {
    private final RecursiveNumericSearchService recursiveNumericSearchService;
    private final ResultNumericQueryService resultService;
    private final SearchConversionService searchConversionService;

    private final MarkerService markerService;
    private final UnitService unitService;

    private final TechnologyResolver technologyResolver;
    private final NumericSearchValidator validator;
    private final NumericSearchConfigurationBuilder configurationBuilder;

    @Autowired
    public NumericSearchService(RecursiveNumericSearchService recursiveNumericSearchService,
                                ResultNumericQueryService resultService, SearchConversionService searchConversionService,
                                MarkerService markerService, UnitService unitService, TechnologyResolver technologyResolver,
                                NumericSearchValidator validator, NumericSearchConfigurationBuilder configurationBuilder) {

        this.recursiveNumericSearchService = recursiveNumericSearchService;
        this.resultService = resultService;
        this.searchConversionService = searchConversionService;

        this.markerService = markerService;
        this.unitService = unitService;

        this.technologyResolver = technologyResolver;
        this.validator = validator;
        this.configurationBuilder = configurationBuilder;
    }

    /**
     * Processes the numeric search options and returns a NumericSearchResult containing the filtered results.
     *
     * @param options the numeric search options
     * @return a NumericSearchResult containing the filtered results
     */
    public NumericSearchResult processNumericSearch(NumericSearchOptions options) {
        validator.validateOptions(options);

        var marker = markerService.getEntity(options.getMarkerName());
        var technologyNames = technologyResolver.resolveTechnologyNames(marker.getName(), options.getFilters());
        var unit = getUnit(options, marker);
        var searchConfig = configurationBuilder.getSearchConfiguration(options);

        return getRecordIds(marker, searchConfig, unit, options, technologyNames);
    }

    private NumericSearchResult getRecordIds(Marker marker, NumericSearchConfiguration searchConfig, Unit unit,
                                             NumericSearchOptions options, Set<String> technologyNames) {
        if (marker.getChildMarkerNames() != null) {
            if (options.getFilters() != null) {
                throw new BadRequestException("Filters are not supported for recursive search");
            }
            var recursiveResults = recursiveNumericSearchService.getResultsForMarkerWithChildren(searchConfig, marker, unit);
            return new NumericSearchResult(null, recursiveResults);
        } else {
            Set<String> recordIds;
            if (searchConfig.getWithTolerance()) {
                recordIds = resultService.searchResultsWithTolerance(searchConfig.getMarkerName(), searchConfig.getMinimum(),
                        searchConfig.getMaximum(), searchConfig.getMinimumWithTolerance(),
                        searchConfig.getMaximumWithTolerance(), searchConfig.getSearchType(), searchConfig.getUseTechnologyDeviation());
            } else {
                recordIds = resultService.searchResults(searchConfig.getMarkerName(), searchConfig.getMinimum(),
                        searchConfig.getMaximum(), searchConfig.getSearchType(), searchConfig.getUseTechnologyDeviation());
            }
            var results = resultService.getResultsByIds(recordIds, marker.getName());
            var filteredResults = ResultFilterUtil.filterResults(results, options.getFilters(), technologyNames);
            return new NumericSearchResult(filteredResults, null);
        }
    }

    private Unit getUnit(NumericSearchOptions options, Marker marker) {
        Unit unit = null;
        if (options.getUnitName() != null) {
            if (marker.getUnitName() == null) {
                throw new BadRequestException("Unit can't be set, marker does not have a unit");
            }

            unit = unitService.getEntity(options.getUnitName());
            if (!Objects.equals(marker.getUnitName(), options.getUnitName())) {
                searchConversionService.convertNumericSearchOptions(unit, marker, options);
            }
        } else {
            // if unitName is null, use marker's unit, otherwise work without a unit
            if (marker.getUnitName() != null) {
                unit = unitService.getEntity(marker.getUnitName());
            }
        }

        return unit;
    }
}
