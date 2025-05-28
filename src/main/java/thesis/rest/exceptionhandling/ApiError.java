package thesis.rest.exceptionhandling;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * ApiError is a class that represents an error response in the API.
 * It contains information about the error message, path, details, timestamp, and any invalid object.
 */
public class ApiError {
    private String message;
    private String path;
    private String details;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object invalidObject;

    public ApiError(String message, String path, String details) {
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public ApiError(String message, String path, String details, Object invalidObject) {
        this.message = message;
        this.path = path;
        this.details = details;
        this.invalidObject = invalidObject;
    }

    public ApiError(String message, String path) {
        this.message = message;
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Object getInvalidObject() {
        return invalidObject;
    }

    public void setInvalidObject(Object invalidObject) {
        this.invalidObject = invalidObject;
    }
}
