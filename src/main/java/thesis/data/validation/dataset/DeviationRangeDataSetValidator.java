package thesis.data.validation.dataset;

import org.springframework.stereotype.Component;
import thesis.data.model.DataSet;
import thesis.data.model.DeviationRange;
import thesis.data.validation.base.DeviationRangeBaseValidator;

@Component
public class DeviationRangeDataSetValidator extends DeviationRangeBaseValidator implements DataSetValidator<DeviationRange> {
    @Override
    public void validateEntity(DataSet dataSet, DeviationRange entity) {
        validate(entity);
    }
}
