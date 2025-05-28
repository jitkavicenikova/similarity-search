package thesis.data.validation.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.enums.AggregationType;
import thesis.data.model.Marker;
import thesis.data.service.MarkerService;
import thesis.data.service.UnitService;
import thesis.exceptions.EntityNotFoundException;
import thesis.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MarkerDatabaseValidatorTest {
    @Mock
    private MarkerService markerService;

    @Mock
    private UnitService unitService;

    @InjectMocks
    private MarkerDatabaseValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateEntity_ShouldPass_WhenMarkerIsValid() {
        Marker marker = new Marker();
        marker.setName("marker1");
        marker.setUnitName("validUnit");
        marker.setChildMarkerNames(List.of("childMarker1", "childMarker2"));
        marker.setAggregationType(AggregationType.SUM);

        Marker childMarker1 = new Marker();
        childMarker1.setAggregationType(AggregationType.SUM);
        Marker childMarker2 = new Marker();
        childMarker2.setAggregationType(AggregationType.SUM);

        when(unitService.existsById("validUnit")).thenReturn(true);
        when(markerService.getEntity("childMarker1")).thenReturn(childMarker1);
        when(markerService.getEntity("childMarker2")).thenReturn(childMarker2);

        validator.validateEntity(marker);

        verify(unitService).existsById("validUnit");
        verify(markerService).getEntity("childMarker1");
        verify(markerService).getEntity("childMarker2");
    }

    @Test
    void validateEntity_ShouldThrowException_WhenUnitDoesNotExist() {
        Marker marker = new Marker();
        marker.setName("marker1");
        marker.setUnitName("invalidUnit");

        when(unitService.existsById("invalidUnit")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> validator.validateEntity(marker),
                "Unit with name invalidUnit does not exist");

        verify(unitService).existsById("invalidUnit");
        verify(markerService, never()).getEntity(anyString());
    }

    @Test
    void validateEntity_ShouldThrowException_WhenChildMarkerDoesNotExist() {
        Marker marker = new Marker();
        marker.setName("marker1");
        marker.setAggregationType(AggregationType.SUM);
        marker.setChildMarkerNames(List.of("nonExistentChildMarker"));

        when(markerService.getEntity("nonExistentChildMarker"))
                .thenThrow(new EntityNotFoundException("Marker with name nonExistentChildMarker not found"));

        assertThrows(EntityNotFoundException.class, () -> validator.validateEntity(marker),
                "Marker with name nonExistentChildMarker not found");

        verify(markerService).getEntity("nonExistentChildMarker");
    }

    @Test
    void validateEntity_ShouldThrowException_WhenChildMarkerAggregationTypeIsDifferent() {
        Marker marker = new Marker();
        marker.setName("marker1");
        marker.setAggregationType(AggregationType.SUM);
        marker.setChildMarkerNames(List.of("childMarker1"));

        Marker childMarker = new Marker();
        childMarker.setAggregationType(AggregationType.AVERAGE); // Different aggregation type

        when(markerService.getEntity("childMarker1")).thenReturn(childMarker);

        assertThrows(ValidationException.class, () -> validator.validateEntity(marker),
                "Child marker with name childMarker1 has a different aggregation type");

        verify(markerService).getEntity("childMarker1");
    }

    @Test
    void validateEntity_ShouldPass_WhenMarkerHasNoUnitAndNoChildMarkers() {
        Marker marker = new Marker();
        marker.setName("marker1");

        validator.validateEntity(marker);

        verifyNoInteractions(unitService, markerService);
    }
}
