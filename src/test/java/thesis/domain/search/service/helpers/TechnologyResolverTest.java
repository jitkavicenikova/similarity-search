package thesis.domain.search.service.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Technology;
import thesis.data.model.TechnologyProperties;
import thesis.data.service.TechnologyService;
import thesis.domain.search.dto.SearchFilters;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TechnologyResolverTest {
    @Mock
    private TechnologyService technologyService;

    private TechnologyResolver technologyResolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        technologyResolver = new TechnologyResolver(technologyService);
    }

    @Test
    void resolveTechnologyNames_ShouldReturnEmptySet_WhenFiltersAreNull() {
        Set<String> result = technologyResolver.resolveTechnologyNames("markerA", null);
        assertEquals(Set.of(), result);
    }

    @Test
    void resolveTechnologyNames_ShouldReturnSingleTechnology_WhenComparableIsFalse() {
        var filters = new SearchFilters();
        filters.setTechnologyName("tech1");
        filters.setIncludeComparableTechnologies(false);

        var tech = new Technology();
        tech.setName("tech1");

        when(technologyService.getEntity("tech1")).thenReturn(tech);

        Set<String> result = technologyResolver.resolveTechnologyNames("markerA", filters);

        assertEquals(Set.of("tech1"), result);
    }

    @Test
    void resolveTechnologyNames_ShouldIncludeComparable_WhenFlagIsTrue() {
        var filters = new SearchFilters();
        filters.setTechnologyName("tech1");
        filters.setIncludeComparableTechnologies(true);

        var prop = new TechnologyProperties();
        prop.setMarkerName("markerA");
        prop.setComparableWith(List.of("tech2", "tech3"));

        var tech = new Technology();
        tech.setName("tech1");
        tech.setProperties(List.of(prop));

        when(technologyService.getEntity("tech1")).thenReturn(tech);

        Set<String> result = technologyResolver.resolveTechnologyNames("markerA", filters);

        assertEquals(Set.of("tech1", "tech2", "tech3"), result);
    }

    @Test
    void resolveTechnologyNamesByThresholds_ShouldReturnMatchingTechnologies() {
        when(technologyService.searchForTechnologyWithMinSpecificity("markerA", 0.9))
                .thenReturn(Set.of("tech1", "tech2"));
        when(technologyService.searchForTechnologyWithMinSensitivity("markerA", 0.8))
                .thenReturn(Set.of("tech2", "tech3"));

        Set<String> result = technologyResolver.resolveTechnologyNamesByThresholds("markerA", 0.8, 0.9);

        assertEquals(Set.of("tech2"), result);
    }

    @Test
    void resolveTechnologyNamesByThresholds_ShouldReturnOnlySensitivityResults_WhenSpecificityIsNull() {
        when(technologyService.searchForTechnologyWithMinSensitivity("markerA", 0.8))
                .thenReturn(Set.of("tech3", "tech4"));

        Set<String> result = technologyResolver.resolveTechnologyNamesByThresholds("markerA", 0.8, null);

        assertEquals(Set.of("tech3", "tech4"), result);
    }

    @Test
    void resolveTechnologyNamesByThresholds_ShouldReturnOnlySpecificityResults_WhenSensitivityIsNull() {
        when(technologyService.searchForTechnologyWithMinSpecificity("markerA", 0.9))
                .thenReturn(Set.of("tech1", "tech2"));

        Set<String> result = technologyResolver.resolveTechnologyNamesByThresholds("markerA", null, 0.9);

        assertEquals(Set.of("tech1", "tech2"), result);
    }
}
