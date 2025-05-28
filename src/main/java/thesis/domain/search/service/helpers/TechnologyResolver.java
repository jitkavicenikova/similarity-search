package thesis.domain.search.service.helpers;

import org.springframework.stereotype.Component;
import thesis.data.service.TechnologyService;
import thesis.domain.search.dto.SearchFilters;
import thesis.utils.SetUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is responsible for resolving technology names based on the provided filters.
 * It interacts with the TechnologyService to fetch technology details and perform filtering.
 */
@Component
public class TechnologyResolver {
    private final TechnologyService technologyService;

    public TechnologyResolver(TechnologyService technologyService) {
        this.technologyService = technologyService;
    }

    /**
     * Resolves technology names based on the provided marker name and search filters.
     *
     * @param markerName the name of the marker
     * @param filters    the search filters to apply
     * @return a set of resolved technology names
     */
    public Set<String> resolveTechnologyNames(String markerName, SearchFilters filters) {
        Set<String> technologyNames = new HashSet<>();

        if (filters == null) {
            return technologyNames;
        }

        if (filters.getTechnologyName() != null) {
            var technology = technologyService.getEntity(filters.getTechnologyName());
            technologyNames.add(technology.getName());
            if (filters.getIncludeComparableTechnologies() != null && filters.getIncludeComparableTechnologies()) {
                if (technology.getProperties() != null) {
                    technology.getProperties().stream()
                            .filter(x -> x.getMarkerName().equals(markerName))
                            .findFirst()
                            .ifPresent(dev -> technologyNames.addAll(dev.getComparableWith()));
                }
            }
        }

        return technologyNames;
    }

    /**
     * Resolves technology names based on the provided marker name and sensitivity/specificity thresholds.
     *
     * @param markerName      the name of the marker
     * @param minSensitivity  the minimum sensitivity threshold
     * @param minSpecificity  the minimum specificity threshold
     * @return a set of resolved technology names
     */
    public Set<String> resolveTechnologyNamesByThresholds(String markerName, Double minSensitivity, Double minSpecificity) {
        Set<String> technologyNames = new HashSet<>();
        if (minSpecificity != null) {
            technologyNames.addAll(technologyService.searchForTechnologyWithMinSpecificity(markerName, minSpecificity));
        }

        if (minSensitivity != null) {
            var sensitivityTechnologies = technologyService.searchForTechnologyWithMinSensitivity(markerName, minSensitivity);
            if (minSpecificity == null) {
                technologyNames.addAll(sensitivityTechnologies);
            } else {
                SetUtils.getIntersection(technologyNames, sensitivityTechnologies);
            }
        }

        return technologyNames;
    }
}
