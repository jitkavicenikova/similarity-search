package thesis.data.validation.database;

import thesis.exceptions.ValidationException;

/**
 * Interface for database validators.
 *
 * @param <T> the type of entity to validate
 */
@FunctionalInterface
public interface DatabaseValidator<T> {
    void validateEntity(T entity);

    /**
     * Validates the entity and throws a ValidationException if the entity is null.
     *
     * @param entity the entity to validate
     * @throws ValidationException if the entity is null
     */
    default void runValidation(T entity) {
        if (entity == null) {
            throw new ValidationException("Null entity found");
        }

        validateEntity(entity);
    }
}