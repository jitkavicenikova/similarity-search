package thesis.exceptions;

/**
 * Custom exception class for handling validation errors.
 * This exception is thrown when an object fails validation checks.
 */
public class ValidationException extends RuntimeException {
    private Object invalidObject;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Object invalidObject) {
        super(message);
        this.invalidObject = invalidObject;
    }

    public Object getInvalidObject() {
        return invalidObject;
    }
}