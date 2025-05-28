package thesis.domain.search.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Marker;
import thesis.data.model.Technology;
import thesis.data.model.Unit;
import thesis.data.service.MarkerService;
import thesis.data.service.UnitService;
import thesis.data.service.query.ResultNumericQueryService;
import thesis.domain.search.dto.NumericSearchConfiguration;
import thesis.domain.search.dto.NumericSearchOptions;
import thesis.domain.search.dto.NumericSearchResult;
import thesis.domain.search.dto.SearchFilters;
import thesis.domain.search.service.helpers.NumericSearchConfigurationBuilder;
import thesis.domain.search.service.helpers.SearchConversionService;
import thesis.domain.search.service.helpers.TechnologyResolver;
import thesis.domain.search.validation.NumericSearchValidator;
import thesis.exceptions.BadRequestException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NumericSearchServiceTest {
    @InjectMocks
    private NumericSearchService numericSearchService;

    @Mock
    private RecursiveNumericSearchService recursiveNumericSearchService;

    @Mock
    private ResultNumericQueryService resultService;

    @Mock
    private SearchConversionService searchConversionService;

    @Mock
    private MarkerService markerService;

    @Mock
    private TechnologyResolver technologyResolver;

    @Mock
    private UnitService unitService;

    @Mock
    private NumericSearchValidator validator;

    @Mock
    private NumericSearchConfigurationBuilder configurationBuilder;

    @Mock
    private Marker marker;

    @Mock
    private Technology technology;

    @Mock
    private Unit unit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processNumericSearch_ShouldReturnNumericSearchResult_WhenValidOptions() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("marker");
        var filters = new SearchFilters("tech", null, null, null, null);
        options.setFilters(filters);

        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", null, 10.0, 20.0, null, null, null, false);
        config.setWithTolerance(false);
        when(configurationBuilder.getSearchConfiguration(options)).thenReturn(config);
        when(markerService.getEntity("marker")).thenReturn(marker);
        when(marker.getChildMarkerNames()).thenReturn(null);
        when(technologyResolver.resolveTechnologyNames("marker", filters)).thenReturn(Set.of("tech"));

        when(marker.getName()).thenReturn("marker");

        Set<String> recordIds = Set.of("record1", "record2");
        when(resultService.searchResults("marker", 10.0, 20.0, null, false)).thenReturn(recordIds);

        NumericSearchResult result = numericSearchService.processNumericSearch(options);

        assertThat(result).isNotNull();
        verify(resultService).searchResults("marker", 10.0, 20.0, null, false);
    }

    @Test
    void processNumericSearch_ShouldThrowBadRequestException_WhenUnitSetAndMarkerHasNoUnit() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("marker");
        options.setUnitName("unit");

        when(markerService.getEntity("marker")).thenReturn(marker);
        when(marker.getUnitName()).thenReturn(null);

        BadRequestException exception = null;
        try {
            numericSearchService.processNumericSearch(options);
        } catch (BadRequestException e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Unit can't be set, marker does not have a unit");
    }
}

