package thesis.data.validation.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thesis.data.model.Technology;
import thesis.exceptions.ValidationException;

import java.util.HashSet;
import java.util.Set;

@Component
public class TechnologyDatabaseValidator implements DatabaseValidator<Technology> {
    private final TechnologyPropertiesDatabaseValidator propertiesDatabaseValidator;

    @Autowired
    public TechnologyDatabaseValidator(TechnologyPropertiesDatabaseValidator propertiesDatabaseValidator) {
        this.propertiesDatabaseValidator = propertiesDatabaseValidator;
    }

    @Override
    public void validateEntity(Technology technology) {
        if (technology.getProperties() != null) {
            if (technology.getProperties().isEmpty()) {
                throw new ValidationException("Technology properties must be null or not empty", technology);
            }

            Set<String> uniqueMarkerNames = new HashSet<>();
            for (var property : technology.getProperties()) {
                propertiesDatabaseValidator.runValidation(property);

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
