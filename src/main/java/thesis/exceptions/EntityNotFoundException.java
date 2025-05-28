package thesis.exceptions;

/**
 * Custom exception class for handling cases where an entity is not found.
 * This exception is thrown when an operation cannot be performed because the entity does not exist.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
