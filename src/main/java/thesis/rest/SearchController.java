package thesis.rest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thesis.data.model.Result;
import thesis.domain.search.dto.*;
import thesis.domain.search.service.AdvancedSearchService;
import thesis.domain.search.service.BoolSearchService;
import thesis.domain.search.service.NumericSearchService;
import thesis.domain.search.service.StringSearchService;
import thesis.utils.JsonLog;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/search")
public class SearchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
    private final NumericSearchService numericSearchService;
    private final StringSearchService stringSearchService;
    private final BoolSearchService boolSearchService;
    private final AdvancedSearchService advancedSearchService;

    @Autowired
    public SearchController(NumericSearchService numericSearchService, StringSearchService stringSearchService,
                            BoolSearchService boolSearchService, AdvancedSearchService advancedSearchService) {
        this.numericSearchService = numericSearchService;
        this.stringSearchService = stringSearchService;
        this.boolSearchService = boolSearchService;
        this.advancedSearchService = advancedSearchService;
    }

    @PostMapping("/numeric")
    public NumericSearchResult numericSearch(@Valid @RequestBody NumericSearchOptions options) {
        LOGGER.info("Processing numeric search with options: {}", JsonLog.toJson(options));
        return numericSearchService.processNumericSearch(options);
    }

    @PostMapping("/string")
    public List<Result> stringSearch(@Valid @RequestBody StringSearchOptions options) {
        LOGGER.info("Processing string search with options: {}", JsonLog.toJson(options));
        return stringSearchService.processStringSearch(options);
    }

    @PostMapping("/bool")
    public List<Result> stringSearch(@Valid @RequestBody BoolSearchOptions options) {
        LOGGER.info("Processing bool search with options: {}", JsonLog.toJson(options));
        return boolSearchService.processBoolSearch(options);
    }

    @PostMapping("/advanced")
    public Set<String> advancedSearch(@Valid @RequestBody AdvancedSearchOptions options) {
        LOGGER.info("Processing advanced search with options: {}", JsonLog.toJson(options));
        return advancedSearchService.processAdvancedSearch(options);
    }
}
