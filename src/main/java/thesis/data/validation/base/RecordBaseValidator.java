package thesis.data.validation.base;

import thesis.data.model.Record;
import thesis.exceptions.ValidationException;
import thesis.utils.JsonUtils;

public abstract class RecordBaseValidator implements BaseValidator<Record> {
    @Override
    public void validate(Record record) {
        if (record.getMetadata() != null && !JsonUtils.isValidJson(record.getMetadata())) {
            throw new ValidationException("Metadata is not a valid JSON", record);
        }
    }
}
