package thesis.domain.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Result;
import thesis.data.model.StringCategory;
import thesis.data.service.MarkerService;
import thesis.data.service.StringCategoryService;
import thesis.data.service.query.ResultStringQueryService;
import thesis.domain.search.dto.StringSearchOptions;
import thesis.domain.search.dto.enums.StringSearchType;
import thesis.domain.search.service.helpers.ResultFilterUtil;
import thesis.domain.search.service.helpers.TechnologyResolver;
import thesis.domain.search.validation.StringSearchValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for processing string search options.
 * It interacts with the ResultStringQueryService and MarkerService to fetch and filter results based on the provided options.
 */
@Service
public class StringSearchService {
    private final ResultStringQueryService resultService;
    private final MarkerService markerService;
    private final StringCategoryService stringCategoryService;
    private final StringSearchValidator validator;
    private final TechnologyResolver technologyResolver;

    @Autowired
    public StringSearchService(ResultStringQueryService resultService, MarkerService markerService,
                               StringCategoryService stringCategoryService, StringSearchValidator validator, TechnologyResolver technologyResolver) {
        this.resultService = resultService;
        this.markerService = markerService;
        this.stringCategoryService = stringCategoryService;
        this.validator = validator;
        this.technologyResolver = technologyResolver;
    }

    /**
     * Processes the string search options and returns a list of results that match the criteria.
     *
     * @param options the string search options
     * @return a list of results that match the search criteria
     */
    public List<Result> processStringSearch(StringSearchOptions options) {
        validator.validateOptions(options);
        var marker = markerService.getEntity(options.getMarkerName());
        var category = getCategory(options);

        if (options.getSearchType() == null) {
            options.setSearchType(StringSearchType.EQUAL);
        }

        if (options.getValue() != null) {
            return processSingleValue(options, category);
        }

        if (options.getValues() != null) {
            return processMultipleValues(options, category);
        }

        if (options.getFilters() != null) {
            var technologyNames = technologyResolver.resolveTechnologyNames(marker.getName(), options.getFilters());

            return ResultFilterUtil.filterResults(resultService.getAllStringResultsForCategory(options.getMarkerName(), category), options.getFilters(), technologyNames);
        }
        return resultService.getAllStringResultsForCategory(options.getMarkerName(), category);
    }

    private List<Result> processSingleValue(StringSearchOptions options, StringCategory category) {
        if (options.getSearchType() == StringSearchType.EQUAL) {
            return resultService.getAllStringResultsForSingleValue(options.getMarkerName(), options.getValue(), category);
        }

        var orderedValues = category.getValues();
        List<String> searchValues = new ArrayList<>();
        var index = orderedValues.indexOf(options.getValue());
        searchValues = switch (options.getSearchType()) {
            case GREATER_THAN -> orderedValues.subList(index + 1, orderedValues.size());
            case GREATER_THAN_OR_EQUAL -> orderedValues.subList(index, orderedValues.size());
            case LESS_THAN -> orderedValues.subList(0, index);
            case LESS_THAN_OR_EQUAL -> orderedValues.subList(0, index + 1);
            default -> searchValues;
        };

        return resultService.getAllStringResultsForMultipleValues(options.getMarkerName(), searchValues, category);
    }

    private List<Result> processMultipleValues(StringSearchOptions options, StringCategory category) {
        return resultService.getAllStringResultsForMultipleValues(options.getMarkerName(), options.getValues(), category);
    }

    private StringCategory getCategory(StringSearchOptions options) {
        if (options.getCategoryName() == null) {
            return null;
        }

        var category = stringCategoryService.getEntity(options.getCategoryName());
        validator.validateCategory(options, category);

        return category;
    }
}
