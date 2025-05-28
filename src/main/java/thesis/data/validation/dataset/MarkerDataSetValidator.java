package thesis.data.validation.dataset;

import org.springframework.stereotype.Component;
import thesis.data.model.DataSet;
import thesis.data.model.Marker;
import thesis.data.validation.base.MarkerBaseValidator;
import thesis.exceptions.ValidationException;

import java.util.Objects;

@Component
public class MarkerDataSetValidator extends MarkerBaseValidator implements DataSetValidator<Marker> {
    @Override
    public void validateEntity(DataSet dataSet, Marker marker) {
        validate(marker);

        if (marker.getUnitName() != null) {
            if (dataSet.getUnits() == null ||
                    dataSet.getUnits().stream().noneMatch(unit -> Objects.equals(unit.getName(), marker.getUnitName()))) {
                throw new ValidationException(String.format("Unit with name %s does not exist", marker.getUnitName()), marker);
            }
        }

        if (marker.getChildMarkerNames() != null) {
            for (var childMarkerId : marker.getChildMarkerNames()) {
                var childMarker = dataSet.getMarkers().stream()
                        .filter(m -> Objects.equals(m.getName(), childMarkerId))
                        .findFirst()
                        .orElseThrow(() -> new ValidationException(String.format("Child marker with name %s does not exist", childMarkerId), marker));

                if (childMarker.getAggregationType() != null && !childMarker.getAggregationType().equals(marker.getAggregationType())) {
                    throw new ValidationException(String.format("Child marker with name %s has a different aggregation type", childMarkerId), marker);
                }
            }
        }
    }
}
