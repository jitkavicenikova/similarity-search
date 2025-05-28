package thesis.data.validation.dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thesis.data.model.DataSet;
import thesis.data.model.TechnologyProperties;
import thesis.data.validation.base.TechnologyPropertiesBaseValidator;
import thesis.exceptions.ValidationException;

import java.util.Objects;

@Component
public class TechnologyPropertiesDataSetValidator extends TechnologyPropertiesBaseValidator implements DataSetValidator<TechnologyProperties> {
    private final DeviationRangeDataSetValidator deviationRangeDataSetValidator;

    @Autowired
    public TechnologyPropertiesDataSetValidator(DeviationRangeDataSetValidator deviationRangeDataSetValidator) {
        this.deviationRangeDataSetValidator = deviationRangeDataSetValidator;
    }

    @Override
    public void validateEntity(DataSet dataSet, TechnologyProperties properties) {
        validate(properties);

        if (properties.getDeviationRanges() != null) {
            properties.getDeviationRanges().forEach(r -> deviationRangeDataSetValidator.runValidation(dataSet, r));
        }

        if (properties.getMarkerName() != null) {
            if (dataSet.getMarkers() == null ||
                    dataSet.getMarkers().stream().noneMatch(marker -> Objects.equals(marker.getName(), properties.getMarkerName()))) {
                throw new ValidationException(String.format("Properties marker with name %s does not exist", properties.getMarkerName()), properties);
            }
        }

        if (properties.getComparableWith() != null) {
            for (var comparableTech : properties.getComparableWith()) {
                if (dataSet.getTechnologies() == null ||
                        dataSet.getTechnologies().stream().noneMatch(t -> Objects.equals(t.getName(), comparableTech))) {
                    throw new ValidationException("Technology with name " + comparableTech + " does not exist", properties);
                }
            }
        }
    }
}
