package thesis.data.validation.dataset;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import thesis.data.model.DataSet;
import thesis.exceptions.ValidationException;

/**
 * Interface for dataset validators.
 *
 * @param <T> the type of entity to validate
 */
@FunctionalInterface
public interface DataSetValidator<T> {
    Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    void validateEntity(DataSet dataSet, T entity);

    /**
     * Validates the entity and throws a ValidationException if the entity is null.
     *
     * @param dataSet the dataset to validate against
     * @param entity  the entity to validate
     * @throws ValidationException if the entity is null or invalid
     */
    default void runValidation(DataSet dataSet, T entity) {
        if (entity == null) {
            throw new ValidationException("Null entity in dataset");
        }

        var violations = VALIDATOR.validate(entity);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.iterator().next().getMessage(), entity);
        }
        validateEntity(dataSet, entity);
    }
}
