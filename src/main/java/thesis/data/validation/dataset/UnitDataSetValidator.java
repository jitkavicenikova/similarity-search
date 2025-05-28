package thesis.data.validation.dataset;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thesis.data.model.DataSet;
import thesis.data.model.Unit;
import thesis.exceptions.ValidationException;

import java.util.HashSet;
import java.util.Set;

@Component
public class UnitDataSetValidator implements DataSetValidator<Unit> {
    private final ConversionDataSetValidator conversionDataSetValidator;

    @Autowired
    public UnitDataSetValidator(ConversionDataSetValidator conversionDataSetValidator) {
        this.conversionDataSetValidator = conversionDataSetValidator;
    }

    @Override
    public void validateEntity(DataSet dataSet, Unit unit) {
        if (unit.getConversions() != null) {
            if (unit.getConversions().isEmpty()) {
                throw new ValidationException("Conversions must be null or not empty", unit);
            }

            validateConversions(dataSet, unit);
        }
    }

    private void validateConversions(DataSet dataSet, Unit unit) {
        Set<Pair<String, String>> uniqueConversions = new HashSet<>();
        for (var conversion : unit.getConversions()) {
            conversionDataSetValidator.runValidation(dataSet, conversion);

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
