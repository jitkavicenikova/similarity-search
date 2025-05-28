package thesis.data.validation.base;

import org.junit.jupiter.api.Test;
import thesis.data.model.Record;
import thesis.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RecordBaseValidatorTest {
    private final RecordBaseValidator validator = new RecordBaseValidator() {
    };

    @Test
    void validate_ShouldThrowException_WhenMetadataIsInvalidJson() {
        var record = new Record();
        record.setMetadata("invalid_json");

        assertThrows(ValidationException.class, () -> validator.validate(record),
                "Expected ValidationException when metadata is not a valid JSON");
    }

    @Test
    void validate_ShouldPass_WhenMetadataIsValidJson() {
        var record = new Record();
        record.setMetadata("{\"key\": \"value\"}");

        validator.validate(record);
    }

    @Test
    void validate_ShouldPass_WhenMetadataIsNull() {
        var record = new Record();
        record.setMetadata(null);

        validator.validate(record);
    }
}