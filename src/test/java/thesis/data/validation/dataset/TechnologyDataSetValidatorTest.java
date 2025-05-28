package thesis.data.validation.dataset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thesis.data.model.DataSet;
import thesis.data.model.Technology;
import thesis.data.model.TechnologyProperties;
import thesis.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TechnologyDataSetValidatorTest {
    private TechnologyDataSetValidator validator;
    private TechnologyPropertiesDataSetValidator mockPropertiesValidator;
    private DataSet dataSet;

    @BeforeEach
    void setUp() {
        mockPropertiesValidator = mock(TechnologyPropertiesDataSetValidator.class);
        validator = new TechnologyDataSetValidator(mockPropertiesValidator);
        dataSet = new DataSet();
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenDuplicateMarkerNameExists() {
        Technology technology = new Technology();
        TechnologyProperties property1 = new TechnologyProperties();
        property1.setMarkerName("marker1");

        TechnologyProperties property2 = new TechnologyProperties();
        property2.setMarkerName("marker1"); // Duplicate marker name

        technology.setProperties(List.of(property1, property2));

        ValidationException exception = assertThrows(ValidationException.class, () ->
                validator.validateEntity(dataSet, technology));

        String expectedMessage = "Duplicate markerName 'marker1' found in technology properties";
        assert exception.getMessage().contains(expectedMessage);
    }

    @Test
    void validate_Entity_ShouldRunPropertiesValidation_WhenNoDuplicatesExist() {
        Technology technology = new Technology();
        TechnologyProperties property1 = new TechnologyProperties();
        property1.setMarkerName("marker1");

        TechnologyProperties property2 = new TechnologyProperties();
        property2.setMarkerName("marker2");

        technology.setProperties(List.of(property1, property2));

        assertDoesNotThrow(() -> validator.validateEntity(dataSet, technology));

        verify(mockPropertiesValidator).runValidation(dataSet, property1);
        verify(mockPropertiesValidator).runValidation(dataSet, property2);
        verifyNoMoreInteractions(mockPropertiesValidator);
    }

    @Test
    void validate_Entity_ShouldNotThrowException_WhenPropertiesListIsNull() {
        Technology technology = new Technology();
        technology.setProperties(null);

        assertDoesNotThrow(() -> validator.validateEntity(dataSet, technology));

        verifyNoInteractions(mockPropertiesValidator);
    }
}
