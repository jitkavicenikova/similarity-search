package thesis.exceptions;

/**
 * Custom exception class for handling cases where an entity is in use.
 * This exception is thrown when an operation cannot be performed because the entity is currently being used.
 */
public class EntityInUseException extends RuntimeException {
    public EntityInUseException(String message) {
        super(message);
    }
}
