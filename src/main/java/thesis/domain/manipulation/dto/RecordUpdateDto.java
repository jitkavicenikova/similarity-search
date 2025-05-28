package thesis.domain.manipulation.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for updating a record.
 *
 * @param idRaw    the ID of the record to update
 * @param metadata the new metadata for the record
 */
public record RecordUpdateDto(
        @NotNull(message = "idRaw cannot be null")
        Integer idRaw,
        @NotNull(message = "metadata cannot be null")
        String metadata
) {
}
