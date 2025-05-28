package thesis.rest.exceptionhandling;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.RedisConnectionException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import thesis.exceptions.*;

import java.io.IOException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiError> handleJsonProcessingException(JsonProcessingException ex, HttpServletRequest request) {
        LOGGER.error(ex.getMessage(), ex);

        var apiError = new ApiError(
                "Parse JSON file error",
                request.getRequestURI(),
                ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiError> handleIOException(IOException ex, HttpServletRequest request) {
        LOGGER.error(ex.getMessage(), ex);

        var apiError = new ApiError(
                "IO error",
                request.getRequestURI(),
                ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex, HttpServletRequest request) {
        LOGGER.error("Validation error", ex);

        var apiError = new ApiError(
                "Validation error",
                request.getRequestURI(),
                ex.getMessage(),
                ex.getInvalidObject());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleJakartaValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        LOGGER.error("Method argument validation error", ex);

        // Extract readable validation messages
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        var apiError = new ApiError(
                "Validation failed",
                request.getRequestURI(),
                errors.stream().findFirst().get());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RedisConnectionException.class)
    public ResponseEntity<ApiError> handleRedisConnectionException(RedisConnectionException ex, HttpServletRequest request) {
        LOGGER.error("Redis connection error", ex);

        var apiError = new ApiError(
                "Redis connection error",
                request.getRequestURI(),
                ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(EntityInUseException.class)
    public ResponseEntity<ApiError> handleEntityInUseException(EntityInUseException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UnitConversionException.class)
    public ResponseEntity<ApiError> handleUnitConversionException(UnitConversionException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ApiError> buildErrorResponse(Exception ex, HttpStatus status, HttpServletRequest request) {
        LOGGER.error(ex.getMessage(), ex);

        var apiError = new ApiError(ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(apiError, status);
    }
}