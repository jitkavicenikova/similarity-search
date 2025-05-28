package thesis.domain.search.service;

import org.springframework.stereotype.Service;
import thesis.data.model.Result;
import thesis.data.service.MarkerService;
import thesis.data.service.query.ResultBoolQueryService;
import thesis.domain.search.dto.BoolSearchOptions;
import thesis.domain.search.service.helpers.ResultFilterUtil;
import thesis.domain.search.service.helpers.TechnologyResolver;
import thesis.domain.search.validation.BoolSearchValidator;

import java.util.List;
import java.util.Set;

/**
 * Service class for processing boolean search options.
 * It interacts with the ResultBoolQueryService and MarkerService to fetch and filter results based on the provided options.
 */
@Service
public class BoolSearchService {
    private final ResultBoolQueryService resultBoolQueryService;
    private final MarkerService markerService;
    private final TechnologyResolver technologyResolver;
    private final BoolSearchValidator validator;

    public BoolSearchService(ResultBoolQueryService resultBoolQueryService, MarkerService markerService,
                             TechnologyResolver technologyResolver, BoolSearchValidator validator) {
        this.resultBoolQueryService = resultBoolQueryService;
        this.markerService = markerService;
        this.technologyResolver = technologyResolver;
        this.validator = validator;
    }

    /**
     * Processes the boolean search options and returns a list of results that match the criteria.
     *
     * @param options the boolean search options
     * @return a list of results that match the search criteria
     */
    public List<Result> processBoolSearch(BoolSearchOptions options) {
        validator.validateOptions(options);
        var marker = markerService.getEntity(options.getMarkerName());

        Set<String> technologyNames = technologyResolver.resolveTechnologyNames(marker.getName(), options.getFilters());
        if (options.getMinSpecificity() != null || options.getMinSensitivity() != null) {
            technologyNames = technologyResolver.resolveTechnologyNamesByThresholds(marker.getName(), options.getMinSensitivity(), options.getMinSpecificity());
            if (technologyNames.isEmpty()) {
                return List.of();
            }
        }

        return ResultFilterUtil.filterResults(resultBoolQueryService.getAllBoolResults(marker.getName(), options.getValue(), technologyNames), options.getFilters());
    }
}
