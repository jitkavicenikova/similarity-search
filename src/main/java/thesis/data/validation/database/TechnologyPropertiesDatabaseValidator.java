package thesis.data.validation.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thesis.data.model.TechnologyProperties;
import thesis.data.service.MarkerService;
import thesis.data.service.TechnologyService;
import thesis.data.validation.base.TechnologyPropertiesBaseValidator;
import thesis.exceptions.EntityNotFoundException;

@Component
public class TechnologyPropertiesDatabaseValidator extends TechnologyPropertiesBaseValidator implements DatabaseValidator<TechnologyProperties> {
    private final DeviationRangeDatabaseValidator deviationRangeDatabaseValidator;
    private final MarkerService markerService;
    private final TechnologyService technologyService;

    @Autowired
    public TechnologyPropertiesDatabaseValidator(DeviationRangeDatabaseValidator deviationRangeDatabaseValidator, MarkerService markerService, TechnologyService technologyService) {
        this.deviationRangeDatabaseValidator = deviationRangeDatabaseValidator;
        this.markerService = markerService;
        this.technologyService = technologyService;
    }

    @Override
    public void validateEntity(TechnologyProperties properties) {
        validate(properties);

        if (properties.getDeviationRanges() != null) {
            properties.getDeviationRanges().forEach(deviationRangeDatabaseValidator::runValidation);
        }

        if (properties.getMarkerName() != null && !markerService.existsById(properties.getMarkerName())) {
            throw new EntityNotFoundException(String.format("Properties marker with name %s does not exist", properties.getMarkerName()));
        }

        if (properties.getComparableWith() != null) {
            for (var comparableTech : properties.getComparableWith()) {
                if (!technologyService.existsById(comparableTech)) {
                    throw new EntityNotFoundException("Technology with name " + comparableTech + " does not exist");
                }
            }
        }
    }
}
