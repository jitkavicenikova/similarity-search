package thesis.data.validation.database;

import org.springframework.stereotype.Component;
import thesis.data.model.DeviationRange;
import thesis.data.validation.base.DeviationRangeBaseValidator;

@Component
public class DeviationRangeDatabaseValidator extends DeviationRangeBaseValidator implements DatabaseValidator<DeviationRange> {
    @Override
    public void validateEntity(DeviationRange deviationRange) {
        validate(deviationRange);
    }
}
