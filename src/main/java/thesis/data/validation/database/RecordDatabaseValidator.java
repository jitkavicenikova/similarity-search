package thesis.data.validation.database;

import org.springframework.stereotype.Component;
import thesis.data.model.Record;
import thesis.data.validation.base.RecordBaseValidator;

@Component
public class RecordDatabaseValidator extends RecordBaseValidator implements DatabaseValidator<Record> {
    @Override
    public void validateEntity(Record entity) {
        validate(entity);
    }
}