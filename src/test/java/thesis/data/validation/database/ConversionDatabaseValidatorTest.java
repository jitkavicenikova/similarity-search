package thesis.data.validation.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Conversion;
import thesis.data.service.MarkerService;
import thesis.data.service.UnitService;
import thesis.exceptions.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ConversionDatabaseValidatorTest {
    @Mock
    private UnitService unitService;

    @Mock
    private MarkerService markerService;

    @InjectMocks
    private ConversionDatabaseValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateEntity_ShouldPass_WhenConversionIsValid() {
        var conversion = new Conversion("validUnit", "validMarker", "x+1");

        when(unitService.existsById("validUnit")).thenReturn(true);
        when(markerService.existsById("validMarker")).thenReturn(true);

        validator.validateEntity(conversion);

        verify(unitService).existsById("validUnit");
        verify(markerService).existsById("validMarker");
    }

    @Test
    void validateEntity_ShouldThrowException_WhenTargetUnitDoesNotExist() {
        var conversion = new Conversion("invalidUnit", "validMarker", "x+1");

        when(unitService.existsById("invalidUnit")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> validator.validateEntity(conversion),
                "Conversion target unit with name invalidUnit not found");

        verify(unitService).existsById("invalidUnit");
        verify(markerService, never()).existsById(anyString());
    }

    @Test
    void validateEntity_ShouldThrowException_WhenMarkerDoesNotExist() {
        var conversion = new Conversion("validUnit", "invalidMarker", "x+1");

        when(unitService.existsById("validUnit")).thenReturn(true);
        when(markerService.existsById("invalidMarker")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> validator.validateEntity(conversion),
                "Conversion marker with name invalidMarker not found");

        verify(unitService).existsById("validUnit");
        verify(markerService).existsById("invalidMarker");
    }
}
