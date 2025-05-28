package thesis.data.validation.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.DeviationRange;
import thesis.data.model.TechnologyProperties;
import thesis.data.service.MarkerService;
import thesis.data.service.TechnologyService;
import thesis.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TechnologyPropertiesDatabaseValidatorTest {
    @InjectMocks
    private TechnologyPropertiesDatabaseValidator validator;

    @Mock
    private DeviationRangeDatabaseValidator deviationRangeDatabaseValidator;

    @Mock
    private MarkerService markerService;

    @Mock
    private TechnologyService technologyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateEntity_ShouldThrowException_WhenMarkerDoesNotExist() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setMarkerName("nonExistentMarker");
        properties.setSensitivity(1.0);

        when(markerService.existsById("nonExistentMarker")).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> validator.validateEntity(properties),
                "Properties marker with name nonExistentMarker does not exist");
    }

    @Test
    void validateEntity_ShouldPass_WhenMarkerExists() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setMarkerName("existingMarker");
        properties.setSensitivity(1.0);

        when(markerService.existsById("existingMarker")).thenReturn(true);

        validator.validateEntity(properties);

        verify(markerService).existsById("existingMarker");
        verifyNoInteractions(technologyService);
    }

    @Test
    void validateEntity_ShouldThrowException_WhenComparableTechnologyDoesNotExist() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setComparableWith(List.of("nonExistentTech"));

        when(technologyService.existsById("nonExistentTech")).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> validator.validateEntity(properties),
                "Technology with name nonExistentTech does not exist");
    }

    @Test
    void validateEntity_ShouldPass_WhenAllComparableTechnologiesExist() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setComparableWith(List.of("tech1", "tech2"));

        when(technologyService.existsById("tech1")).thenReturn(true);
        when(technologyService.existsById("tech2")).thenReturn(true);

        validator.validateEntity(properties);

        verify(technologyService).existsById("tech1");
        verify(technologyService).existsById("tech2");
        verifyNoMoreInteractions(technologyService);
    }

    @Test
    void validateEntity_ShouldCallDeviationRangeValidator_ForEachDeviationRange() {
        TechnologyProperties properties = new TechnologyProperties();
        var deviationRange1 = mock(DeviationRange.class);
        var deviationRange2 = mock(DeviationRange.class);
        properties.setDeviationRanges(new ArrayList<>(List.of(deviationRange1, deviationRange2)));
        properties.setIsPercentage(true);

        validator.validateEntity(properties);

        verify(deviationRangeDatabaseValidator).runValidation(deviationRange1);
        verify(deviationRangeDatabaseValidator).runValidation(deviationRange2);
        verifyNoMoreInteractions(deviationRangeDatabaseValidator);
    }

    @Test
    void validateEntity_ShouldPass_WhenAllConditionsAreMet() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setMarkerName("existingMarker");
        properties.setComparableWith(List.of("tech1"));
        var deviationRange = mock(DeviationRange.class);
        properties.setDeviationRanges(new ArrayList<>(List.of(deviationRange)));
        properties.setIsPercentage(true);

        when(markerService.existsById("existingMarker")).thenReturn(true);
        when(technologyService.existsById("tech1")).thenReturn(true);

        validator.validateEntity(properties);

        verify(markerService).existsById("existingMarker");
        verify(technologyService).existsById("tech1");
        verify(deviationRangeDatabaseValidator).runValidation(deviationRange);
    }
}
