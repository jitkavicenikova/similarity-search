package thesis.data.validation.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Conversion;
import thesis.data.model.Unit;
import thesis.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class UnitDatabaseValidatorTest {
    @InjectMocks
    private UnitDatabaseValidator unitDatabaseValidator;

    @Mock
    private ConversionDatabaseValidator conversionDatabaseValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateEntity_ShouldThrowValidationException_WhenConversionsListIsEmpty() {
        Unit unit = new Unit();
        unit.setConversions(List.of());

        assertThrows(ValidationException.class,
                () -> unitDatabaseValidator.validateEntity(unit),
                "Conversions must be null or not empty");
    }

    @Test
    void validateEntity_ShouldPass_WhenNoDuplicateConversionsExist() {
        Conversion conversion1 = new Conversion("marker1", "unitA", "x+1");
        Conversion conversion2 = new Conversion("marker2", "unitB", "x+1");
        Unit unit = new Unit();
        unit.setConversions(List.of(conversion1, conversion2));

        unitDatabaseValidator.validateEntity(unit);

        verify(conversionDatabaseValidator).runValidation(conversion1);
        verify(conversionDatabaseValidator).runValidation(conversion2);
        verifyNoMoreInteractions(conversionDatabaseValidator);
    }

    @Test
    void validateEntity_ShouldThrowValidationException_WhenDuplicateConversionExists() {
        Conversion conversion1 = new Conversion("marker1", "unitA", "x+1");
        Conversion duplicateConversion = new Conversion("marker1", "unitA", "x+1");
        Unit unit = new Unit();
        unit.setConversions(List.of(conversion1, duplicateConversion));

        assertThrows(ValidationException.class,
                () -> unitDatabaseValidator.validateEntity(unit),
                "Duplicate conversion for marker 'marker1' and target unit 'unitA' found");
    }

    @Test
    void validateEntity_ShouldCallConversionDatabaseValidator_ForAllConversions() {
        Conversion conversion1 = new Conversion("marker1", "unitA", "x+1");
        Conversion conversion2 = new Conversion("marker2", "unitB", "x+1");
        Unit unit = new Unit();
        unit.setConversions(List.of(conversion1, conversion2));

        unitDatabaseValidator.validateEntity(unit);

        verify(conversionDatabaseValidator).runValidation(conversion1);
        verify(conversionDatabaseValidator).runValidation(conversion2);
        verifyNoMoreInteractions(conversionDatabaseValidator);
    }
}
