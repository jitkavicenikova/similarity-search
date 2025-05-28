package thesis.domain.manipulation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Conversion;
import thesis.data.model.Unit;
import thesis.data.service.UnitService;
import thesis.data.validation.database.ConversionDatabaseValidator;
import thesis.data.validation.database.UnitDatabaseValidator;

@Service
public class UnitManipulationService extends BaseEntityManipulationService<Unit> {
    private final ConversionDatabaseValidator conversionDatabaseValidator;
    private final UnitService unitService;

    @Autowired
    public UnitManipulationService(UnitService unitService, UnitDatabaseValidator unitDatabaseValidator,
                                   ConversionDatabaseValidator conversionDatabaseValidator) {
        super(unitService, unitDatabaseValidator);
        this.conversionDatabaseValidator = conversionDatabaseValidator;
        this.unitService = unitService;
    }

    public Unit addConversion(String unitName, Conversion conversion) {
        conversionDatabaseValidator.runValidation(conversion);
        return unitService.addConversion(unitName, conversion);
    }
}
