package thesis.data.validation.dataset;

import org.springframework.stereotype.Component;
import thesis.data.model.DataSet;
import thesis.data.model.Result;
import thesis.data.validation.base.ResultBaseValidator;
import thesis.exceptions.ValidationException;

import java.util.Objects;

@Component
public class ResultDataSetValidator extends ResultBaseValidator implements DataSetValidator<Result> {
    @Override
    public void validateEntity(DataSet dataSet, Result result) {
        validate(result);

        if (dataSet.getMarkers() == null) {
            throw new ValidationException("Marker with name " + result.getMarkerName() + " does not exist", result);
        }

        var marker = dataSet.getMarkers().stream()
                .filter(m -> Objects.equals(m.getName(), result.getMarkerName()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Marker with name " + result.getMarkerName() + " does not exist", result));

        if (marker.getAggregationType() != null) {
            throw new ValidationException("Marker with children cannot be used in result", result);
        }

        if (dataSet.getRecords() == null ||
                dataSet.getRecords().stream().noneMatch(record -> Objects.equals(record.getIdRaw(), result.getRecordIdRaw()))) {
            throw new ValidationException("Record with id " + result.getRecordIdRaw() + " does not exist", result);
        }

        if (result.getUnitRawName() != null && (
                dataSet.getUnits() == null ||
                        dataSet.getUnits().stream().noneMatch(unit -> Objects.equals(unit.getName(), result.getUnitRawName())))) {
            throw new ValidationException("Unit raw with name " + result.getUnitRawName() + " does not exist", result);
        }

        if (result.getTechnologyName() != null && (
                dataSet.getTechnologies() == null ||
                        dataSet.getTechnologies().stream().noneMatch(technology -> Objects.equals(technology.getName(), result.getTechnologyName())))) {
            throw new ValidationException("Technology with name " + result.getTechnologyName() + " does not exist", result);
        }

        if (result.getStringValueCategory() != null) {
            var categories = dataSet.getStringCategories();

            if (categories == null || categories.stream().noneMatch(c -> Objects.equals(c.getName(), result.getStringValueCategory()))) {
                throw new ValidationException("String value category with name " + result.getStringValueCategory() + " does not exist", result);
            }

            if (result.getStringValue() != null) {
                var category = categories.stream()
                        .filter(c -> Objects.equals(c.getName(), result.getStringValueCategory()))
                        .findFirst()
                        .orElseThrow(() -> new ValidationException("String value category with name " + result.getStringValueCategory() + " does not exist", result));

                if (!category.getValues().contains(result.getStringValue())) {
                    throw new ValidationException("Value " + result.getStringValue() + " is not in category " + result.getStringValueCategory(), result);
                }
            }
        }
    }
}
