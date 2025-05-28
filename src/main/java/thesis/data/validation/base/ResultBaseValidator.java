package thesis.data.validation.base;

import thesis.data.model.Result;
import thesis.exceptions.ValidationException;

public abstract class ResultBaseValidator implements BaseValidator<Result> {
    @Override
    public void validate(Result result) {
        if (result.getStringValueCategory() != null && result.getStringValue() == null) {
            throw new ValidationException("Result with stringValueCategory must have stringValue", result);
        }

        if (result.getMin() != null && result.getMax() != null && result.getMin() > result.getMax()) {
            throw new ValidationException("Min value must be less than max value", result);
        }

        validateValues(result);
    }

    private void validateValues(Result result) {
        int filledValues = 0;

        if (result.getMin() != null || result.getMax() != null) filledValues++;
        if (result.getStringValue() != null && !result.getStringValue().trim().isEmpty()) filledValues++;
        if (result.getBooleanValue() != null) filledValues++;

        if (filledValues == 0) {
            throw new ValidationException("Result must have at least one of min/max, stringValue, or booleanValue", result);
        } else if (filledValues > 1) {
            throw new ValidationException("Result can only have one of min/max, stringValue, or booleanValue", result);
        }
    }
}
