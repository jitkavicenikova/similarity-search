package thesis.data.validation.dataset;

import org.springframework.stereotype.Component;
import thesis.data.model.Conversion;
import thesis.data.model.DataSet;
import thesis.data.validation.base.ConversionBaseValidator;
import thesis.exceptions.ValidationException;

import java.util.Objects;

@Component
public class ConversionDataSetValidator extends ConversionBaseValidator implements DataSetValidator<Conversion> {
    @Override
    public void validateEntity(DataSet dataSet, Conversion conversion) {
        validate(conversion);

        if (dataSet.getUnits() == null ||
                dataSet.getUnits().stream().noneMatch(u -> Objects.equals(u.getName(), conversion.getTargetUnitName()))) {
            throw new ValidationException(String.format("Conversion target unit with name %s not found", conversion.getTargetUnitName()), conversion);
        }

        if (dataSet.getMarkers() == null ||
                dataSet.getMarkers().stream().noneMatch(m -> Objects.equals(m.getName(), conversion.getMarkerName()))) {
            throw new ValidationException(String.format("Conversion marker with name %s not found", conversion.getMarkerName()), conversion);
        }
    }
}
