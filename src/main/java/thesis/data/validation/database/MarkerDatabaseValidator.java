package thesis.data.validation.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thesis.data.model.Marker;
import thesis.data.service.MarkerService;
import thesis.data.service.UnitService;
import thesis.data.validation.base.MarkerBaseValidator;
import thesis.exceptions.EntityNotFoundException;
import thesis.exceptions.ValidationException;

@Component
public class MarkerDatabaseValidator extends MarkerBaseValidator implements DatabaseValidator<Marker> {
    private final MarkerService markerService;
    private final UnitService unitService;

    @Autowired
    public MarkerDatabaseValidator(MarkerService markerService, UnitService unitService) {
        this.markerService = markerService;
        this.unitService = unitService;
    }

    @Override
    public void validateEntity(Marker marker) {
        validate(marker);

        if (marker.getUnitName() != null && !unitService.existsById(marker.getUnitName())) {
            throw new EntityNotFoundException(String.format("Unit with name %s does not exist", marker.getUnitName()));
        }

        if (marker.getChildMarkerNames() != null) {
            for (var childMarkerId : marker.getChildMarkerNames()) {
                var childMarker = markerService.getEntity(childMarkerId);

                if (childMarker.getAggregationType() != null && !childMarker.getAggregationType().equals(marker.getAggregationType())) {
                    throw new ValidationException(String.format("Child marker with name %s has a different aggregation type", childMarkerId), marker);
                }
            }
        }
    }
}
