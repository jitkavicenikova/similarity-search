package thesis.domain.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Result;
import thesis.domain.search.dto.AdvancedSearchOptions;
import thesis.domain.search.dto.AggregatedResult;
import thesis.utils.SetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for processing advanced search options.
 * It combines results from different search services (numeric, string, and boolean) to provide a comprehensive search result.
 */
@Service
public class AdvancedSearchService {
    private final NumericSearchService numericSearchService;
    private final StringSearchService stringSearchService;
    private final BoolSearchService boolSearchService;

    @Autowired
    public AdvancedSearchService(NumericSearchService numericSearchService, StringSearchService stringSearchService,
                                 BoolSearchService boolSearchService) {
        this.numericSearchService = numericSearchService;
        this.stringSearchService = stringSearchService;
        this.boolSearchService = boolSearchService;
    }

    /**
     * Processes the advanced search options and returns a set of record IDs that match the criteria.
     *
     * @param options the advanced search options
     * @return a set of record IDs that match the search criteria
     */
    public Set<String> processAdvancedSearch(AdvancedSearchOptions options) {
        List<Set<String>> recordIdSets = new ArrayList<>();

        addRecordIdsFromBoolSearch(options, recordIdSets);
        addRecordIdsFromNumericSearch(options, recordIdSets);
        addRecordIdsFromStringSearch(options, recordIdSets);

        return recordIdSets.stream()
                .reduce(SetUtils::getIntersection)
                .orElse(Set.of());
    }

    private void addRecordIdsFromBoolSearch(AdvancedSearchOptions options, List<Set<String>> recordIdSets) {
        if (options.getBoolOptions() != null) {
            options.getBoolOptions().forEach(boolOption -> {
                List<Result> results = boolSearchService.processBoolSearch(boolOption);
                recordIdSets.add(results.stream().map(Result::getRecordId).collect(Collectors.toSet()));
            });
        }
    }

    private void addRecordIdsFromNumericSearch(AdvancedSearchOptions options, List<Set<String>> recordIdSets) {
        if (options.getNumericOptions() != null) {
            options.getNumericOptions().forEach(numericOption -> {
                var numericSearchResult = numericSearchService.processNumericSearch(numericOption);

                Set<String> recordIds;

                if (numericSearchResult.getResults() != null) {
                    recordIds = numericSearchResult.getResults().stream()
                            .map(Result::getRecordId)
                            .collect(Collectors.toSet());
                } else {
                    recordIds = numericSearchResult.getAggregatedResults().stream()
                            .map(AggregatedResult::getRecordId)
                            .collect(Collectors.toSet());
                }

                recordIdSets.add(recordIds);
            });
        }
    }

    private void addRecordIdsFromStringSearch(AdvancedSearchOptions options, List<Set<String>> recordIdSets) {
        if (options.getStringOptions() != null) {
            options.getStringOptions().forEach(stringOption -> {
                List<Result> results = stringSearchService.processStringSearch(stringOption);
                recordIdSets.add(results.stream().map(Result::getRecordId).collect(Collectors.toSet()));
            });
        }
    }
}
