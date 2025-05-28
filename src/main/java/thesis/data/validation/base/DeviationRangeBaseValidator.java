package thesis.data.validation.base;

import thesis.data.model.DeviationRange;
import thesis.exceptions.ValidationException;

public abstract class DeviationRangeBaseValidator implements BaseValidator<DeviationRange> {
    @Override
    public void validate(DeviationRange deviationRange) {
        if (deviationRange.getFrom() > deviationRange.getTo()) {
            throw new ValidationException(String.format("Deviation range 'from' (%.2f) cannot be greater than 'to' (%.2f)", deviationRange.getFrom(), deviationRange.getTo()), deviationRange);
        }
    }
}
