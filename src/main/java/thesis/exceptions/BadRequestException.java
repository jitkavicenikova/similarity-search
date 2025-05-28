package thesis.exceptions;

/**
 * Custom exception class for handling bad requests.
 * This exception is thrown when a request does not meet the expected criteria or format.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
