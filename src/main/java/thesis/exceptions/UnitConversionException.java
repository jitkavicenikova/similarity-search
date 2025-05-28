package thesis.exceptions;

/**
 * Custom exception class for handling unit conversion errors.
 * This exception is thrown when a unit conversion operation fails.
 */
public class UnitConversionException extends RuntimeException {
    public UnitConversionException(String message) {
        super(message);
    }
}
