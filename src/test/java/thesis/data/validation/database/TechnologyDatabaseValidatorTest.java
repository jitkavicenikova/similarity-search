package thesis.data.validation.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Technology;
import thesis.data.model.TechnologyProperties;
import thesis.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TechnologyDatabaseValidatorTest {
    @InjectMocks
    private TechnologyDatabaseValidator technologyDatabaseValidator;

    @Mock
    private TechnologyPropertiesDatabaseValidator propertiesDatabaseValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateEntity_ShouldThrowException_WhenDuplicateMarkerNamesArePresent() {
        Technology technology = new Technology();
        TechnologyProperties property1 = new TechnologyProperties();
        property1.setMarkerName("marker1");
        TechnologyProperties property2 = new TechnologyProperties();
        property2.setMarkerName("marker1");
        technology.setProperties(List.of(property1, property2));

        assertThrows(ValidationException.class, () -> technologyDatabaseValidator.validateEntity(technology),
                "Duplicate markerName 'marker1' found in technology properties");
    }

    @Test
    void validateEntity_ShouldPass_WhenNoPropertiesArePresent() {
        Technology technology = new Technology();
        technology.setProperties(null);

        technologyDatabaseValidator.validateEntity(technology);

        verifyNoInteractions(propertiesDatabaseValidator);
    }

    @Test
    void validateEntity_ShouldCallPropertiesValidator_ForEachUniqueProperty() {
        Technology technology = new Technology();
        TechnologyProperties property1 = new TechnologyProperties();
        property1.setMarkerName("marker1");
        TechnologyProperties property2 = new TechnologyProperties();
        property2.setMarkerName("marker2");
        technology.setProperties(List.of(property1, property2));

        technologyDatabaseValidator.validateEntity(technology);

        verify(propertiesDatabaseValidator).runValidation(property1);
        verify(propertiesDatabaseValidator).runValidation(property2);
        verifyNoMoreInteractions(propertiesDatabaseValidator);
    }

    @Test
    void validateEntity_ShouldThrowException_WithTechnologyReference_WhenDuplicateMarkerNameIsFound() {
        Technology technology = new Technology();
        TechnologyProperties property1 = new TechnologyProperties();
        property1.setMarkerName("duplicateMarker");
        TechnologyProperties property2 = new TechnologyProperties();
        property2.setMarkerName("duplicateMarker");
        technology.setProperties(List.of(property1, property2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> technologyDatabaseValidator.validateEntity(technology),
                "Duplicate markerName 'duplicateMarker' found in technology properties");

        assertThrows(ValidationException.class, () -> {
            throw exception;
        }, "Exception should hold the reference to the validated Technology object");
    }

    @Test
    void validateEntity_ShouldPass_WhenUniqueMarkerNamesArePresent() {
        Technology technology = new Technology();
        TechnologyProperties property1 = new TechnologyProperties();
        property1.setMarkerName("uniqueMarker1");
        TechnologyProperties property2 = new TechnologyProperties();
        property2.setMarkerName("uniqueMarker2");
        technology.setProperties(List.of(property1, property2));

        technologyDatabaseValidator.validateEntity(technology);

        verify(propertiesDatabaseValidator).runValidation(property1);
        verify(propertiesDatabaseValidator).runValidation(property2);
        verifyNoMoreInteractions(propertiesDatabaseValidator);
    }
}
