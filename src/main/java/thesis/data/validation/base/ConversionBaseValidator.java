package thesis.data.validation.base;

import thesis.data.model.Conversion;
import thesis.exceptions.ValidationException;
import thesis.utils.FormulaEvaluator;

public abstract class ConversionBaseValidator implements BaseValidator<Conversion> {
    @Override
    public void validate(Conversion conversion) {
        if (!FormulaEvaluator.isValidFormula(conversion.getFormula())) {
            throw new ValidationException(String.format("Invalid conversion formula %s", conversion.getFormula()), conversion);
        }
    }
}
