package thesis.data.validation.dataset;

import org.springframework.stereotype.Component;
import thesis.data.model.DataSet;
import thesis.data.model.Record;
import thesis.data.validation.base.RecordBaseValidator;

@Component
public class RecordDataSetValidator extends RecordBaseValidator implements DataSetValidator<Record> {
    @Override
    public void validateEntity(DataSet dataSet, Record entity) {
        validate(entity);
    }
}
