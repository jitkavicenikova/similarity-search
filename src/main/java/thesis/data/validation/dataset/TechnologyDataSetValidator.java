package thesis.data.validation.dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thesis.data.model.DataSet;
import thesis.data.model.Technology;
import thesis.exceptions.ValidationException;

import java.util.HashSet;
import java.util.Set;

@Component
public class TechnologyDataSetValidator implements DataSetValidator<Technology> {
    private final TechnologyPropertiesDataSetValidator propertiesDataSetValidator;

    @Autowired
    public TechnologyDataSetValidator(TechnologyPropertiesDataSetValidator propertiesDataSetValidator) {
        this.propertiesDataSetValidator = propertiesDataSetValidator;
    }

    @Override
    public void validateEntity(DataSet dataSet, Technology technology) {
        if (technology.getProperties() != null) {
            if (technology.getProperties().isEmpty()) {
                throw new ValidationException("Technology properties must be null or not empty", technology);
            }

            Set<String> uniqueMarkerNames = new HashSet<>();
            for (var property : technology.getProperties()) {
                propertiesDataSetValidator.runValidation(dataSet, property);

                if (!uniqueMarkerNames.add(property.getMarkerName())) {
                    throw new ValidationException(
                            String.format("Duplicate markerName '%s' found in technology properties", property.getMarkerName()),
                            technology
                    );
                }
            }
        }
    }
}
