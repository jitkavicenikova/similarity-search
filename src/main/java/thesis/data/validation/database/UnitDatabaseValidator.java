package thesis.data.validation.database;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thesis.data.model.Unit;
import thesis.exceptions.ValidationException;

import java.util.HashSet;
import java.util.Set;

@Component
public class UnitDatabaseValidator implements DatabaseValidator<Unit> {
    private final ConversionDatabaseValidator conversionDatabaseValidator;

    @Autowired
    public UnitDatabaseValidator(ConversionDatabaseValidator conversionDatabaseValidator) {
        this.conversionDatabaseValidator = conversionDatabaseValidator;
    }

    @Override
    public void validateEntity(Unit unit) {
        if (unit.getConversions() != null) {
            if (unit.getConversions().isEmpty()) {
                throw new ValidationException("Conversions must be null or not empty", unit);
            }

            validateConversions(unit);
        }
    }

    private void validateConversions(Unit unit) {
        Set<Pair<String, String>> uniqueConversions = new HashSet<>();
        for (var conversion : unit.getConversions()) {
            conversionDatabaseValidator.runValidation(conversion);

            var key = Pair.of(conversion.getMarkerName(), conversion.getTargetUnitName());
            if (!uniqueConversions.add(key)) {
                throw new ValidationException(
                        String.format("Duplicate conversion for marker '%s' and target unit '%s' found",
                                conversion.getMarkerName(), conversion.getTargetUnitName()),
                        unit
                );
            }
        }
    }
}
