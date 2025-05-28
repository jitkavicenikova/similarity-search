package thesis.data.validation.base;

/**
 * Base interface for validators.
 *
 * @param <T> the type of entity to validate
 */
@FunctionalInterface
public interface BaseValidator<T> {
    /**
     * Executes basic validation on the given entity.
     *
     * @param entity the entity to validate
     * @throws IllegalArgumentException if the entity is invalid
     */
    void validate(T entity);
}

