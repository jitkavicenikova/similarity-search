package thesis.data.validation.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thesis.data.model.Conversion;
import thesis.data.service.MarkerService;
import thesis.data.service.UnitService;
import thesis.data.validation.base.ConversionBaseValidator;
import thesis.exceptions.EntityNotFoundException;

@Component
public class ConversionDatabaseValidator extends ConversionBaseValidator implements DatabaseValidator<Conversion> {
    private final UnitService unitService;
    private final MarkerService markerService;

    @Autowired
    public ConversionDatabaseValidator(UnitService unitService, MarkerService markerService) {
        this.unitService = unitService;
        this.markerService = markerService;
    }

    @Override
    public void validateEntity(Conversion conversion) {
        validate(conversion);

        if (!unitService.existsById(conversion.getTargetUnitName())) {
            throw new EntityNotFoundException(String.format("Conversion target unit with name %s not found", conversion.getTargetUnitName()));
        }

        if (!markerService.existsById(conversion.getMarkerName())) {
            throw new EntityNotFoundException(String.format("Conversion marker with name %s not found", conversion.getMarkerName()));
        }
    }
}
