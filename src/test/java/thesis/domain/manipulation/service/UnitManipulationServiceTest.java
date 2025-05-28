package thesis.domain.manipulation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Conversion;
import thesis.data.model.Unit;
import thesis.data.service.UnitService;
import thesis.data.validation.database.ConversionDatabaseValidator;
import thesis.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UnitManipulationServiceTest {
    @Mock
    private UnitService unitService;

    @Mock
    private ConversionDatabaseValidator conversionDatabaseValidator;

    @InjectMocks
    private UnitManipulationService unitManipulationService;

    private Unit unit;
    private Conversion conversion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        unit = new Unit();
        conversion = new Conversion("targetUnit", "markerName", "x * 1.5");
    }

    @Test
    void addConversion_ShouldValidateAndAddConversion_WhenValidConversionIsGiven() {
        when(unitService.addConversion("unit1", conversion)).thenReturn(unit);

        Unit result = unitManipulationService.addConversion("unit1", conversion);

        verify(conversionDatabaseValidator).runValidation(conversion);
        verify(unitService).addConversion("unit1", conversion);
        assertEquals(unit, result);
    }

    @Test
    void addConversion_ShouldThrowValidationException_WhenInvalidConversionIsGiven() {
        doThrow(new ValidationException("Invalid conversion", conversion))
                .when(conversionDatabaseValidator)
                .runValidation(conversion);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> unitManipulationService.addConversion("unit1", conversion));
        assertEquals("Invalid conversion", exception.getMessage());
        verify(conversionDatabaseValidator).runValidation(conversion);
        verifyNoInteractions(unitService); // Ensure service is not called if validation fails
    }
}

