package thesis.data.validation.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thesis.data.model.Result;
import thesis.data.service.MarkerService;
import thesis.data.service.StringCategoryService;
import thesis.data.service.TechnologyService;
import thesis.data.service.UnitService;
import thesis.data.validation.base.ResultBaseValidator;
import thesis.exceptions.EntityNotFoundException;
import thesis.exceptions.ValidationException;

@Component
public class ResultDatabaseValidator extends ResultBaseValidator implements DatabaseValidator<Result> {
    private final MarkerService markerService;
    private final UnitService unitService;
    private final TechnologyService technologyService;
    private final StringCategoryService stringCategoryService;

    @Autowired
    public ResultDatabaseValidator(MarkerService markerService, UnitService unitService,
                                   TechnologyService technologyService, StringCategoryService stringCategoryService) {
        this.markerService = markerService;
        this.unitService = unitService;
        this.technologyService = technologyService;
        this.stringCategoryService = stringCategoryService;
    }

    @Override
    public void validateEntity(Result result) {
        validate(result);

        var marker = markerService.getEntity(result.getMarkerName());
        if (marker.getAggregationType() != null) {
            throw new ValidationException("Marker with children cannot be used in result");
        }

        if (result.getUnitRawName() != null && !unitService.existsById(result.getUnitRawName())) {
            throw new EntityNotFoundException("Unit raw with name " + result.getUnitRawName() + " does not exist");
        }

        if (result.getTechnologyName() != null && !technologyService.existsById(result.getTechnologyName())) {
            throw new EntityNotFoundException("Technology with name " + result.getTechnologyName() + " does not exist");
        }

        if (result.getStringValueCategory() != null) {
            var category = stringCategoryService.getEntity(result.getStringValueCategory());
            if (!category.getValues().contains(result.getStringValue())) {
                throw new ValidationException("Value " + result.getStringValue() + " is not in category " + result.getStringValueCategory(), result);
            }
        }
    }
}
