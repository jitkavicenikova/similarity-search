package thesis.data.validation.base;

import thesis.data.model.Marker;
import thesis.exceptions.ValidationException;

import java.util.HashSet;
import java.util.Set;

public abstract class MarkerBaseValidator implements BaseValidator<Marker> {
    @Override
    public void validate(Marker marker) {
        if (marker.getChildMarkerNames() != null && marker.getChildMarkerNames().isEmpty()) {
            throw new ValidationException("childMarkerNames must be null or not empty", marker);
        }

        if (marker.getAggregationType() != null && marker.getChildMarkerNames() == null) {
            throw new ValidationException("AggregationType must not be set when childMarkerNames are not set", marker);
        }

        if (marker.getChildMarkerNames() != null) {
            if (marker.getChildMarkerNames().contains(marker.getName())) {
                throw new ValidationException("Marker cannot list itself as a child marker", marker);
            }

            if (marker.getAggregationType() == null) {
                throw new ValidationException("AggregationType must be set when childMarkerNames are set", marker);
            }

            Set<String> uniqueValues = new HashSet<>();
            for (String value : marker.getChildMarkerNames()) {
                if (value == null || value.isBlank()) {
                    throw new ValidationException("childMarkerNames contains null or empty values", marker);
                }
                if (!uniqueValues.add(value)) {
                    throw new ValidationException("childMarkerNames contains duplicates", marker);
                }
            }
        }
    }
}
