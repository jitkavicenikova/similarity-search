package thesis.domain.manipulation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating a result.
 *
 * @param recordId           the ID of the record
 * @param markerName         the name of the marker
 * @param recordIdRaw        the raw ID of the record
 * @param min                the minimum value
 * @param max                the maximum value
 * @param stringValue        the string value
 * @param stringValueCategory the category of the string value
 * @param booleanValue       the boolean value
 * @param technologyName     the name of the technology
 * @param sample             the sample name
 * @param minRaw             the minimum raw value
 * @param maxRaw             the maximum raw value
 * @param unitRawName        the name of the raw unit
 * @param timestamp          the timestamp of the result
 */
public record ResultCreateDto(
        @NotBlank(message = "recordId cannot be empty") String recordId,
        @NotBlank(message = "markerName cannot be empty") String markerName,
        Integer recordIdRaw,
        Double min,
        Double max,
        @Size(max = 100, message = "stringValue must be at most 100 characters long") String stringValue,
        String stringValueCategory,
        Boolean booleanValue,
        String technologyName,
        @Size(max = 100, message = "sample must be at most 100 characters long") String sample,
        Double minRaw,
        Double maxRaw,
        String unitRawName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp
) {
}