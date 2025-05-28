package thesis.data.validation.dataset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thesis.data.model.*;
import thesis.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TechnologyPropertiesDataSetValidatorTest {
    private TechnologyPropertiesDataSetValidator validator;
    private DeviationRangeDataSetValidator mockDeviationRangeValidator;
    private DataSet dataSet;

    @BeforeEach
    void setUp() {
        mockDeviationRangeValidator = mock(DeviationRangeDataSetValidator.class);
        validator = new TechnologyPropertiesDataSetValidator(mockDeviationRangeValidator);
        dataSet = new DataSet();
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenMarkerDoesNotExistInDataSet() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setMarkerName("nonExistentMarker");
        properties.setSensitivity(0.5);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateEntity(dataSet, properties));

        assert exception.getMessage().contains("Properties marker with name nonExistentMarker does not exist");
    }

    @Test
    void validate_Entity_ShouldNotThrowException_WhenMarkerExistsInDataSet() {
        Marker marker = new Marker();
        marker.setName("existentMarker");
        dataSet.setMarkers(List.of(marker));

        TechnologyProperties properties = new TechnologyProperties();
        properties.setMarkerName("existentMarker");
        properties.setSensitivity(0.5);

        assertDoesNotThrow(() -> validator.validateEntity(dataSet, properties));
    }

    @Test
    void validate_Entity_ShouldThrowException_WhenComparableTechnologyDoesNotExist() {
        TechnologyProperties properties = new TechnologyProperties();
        properties.setComparableWith(List.of("nonExistentTech"));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateEntity(dataSet, properties));

        assert exception.getMessage().contains("Technology with name nonExistentTech does not exist");
    }

    @Test
    void validate_Entity_ShouldNotThrowException_WhenComparableTechnologiesExist() {
        Technology tech1 = new Technology();
        tech1.setName("Tech1");

        Technology tech2 = new Technology();
        tech2.setName("Tech2");

        dataSet.setTechnologies(List.of(tech1, tech2));

        TechnologyProperties properties = new TechnologyProperties();
        properties.setComparableWith(List.of("Tech1", "Tech2"));

        assertDoesNotThrow(() -> validator.validateEntity(dataSet, properties));
    }

    @Test
    void validate_Entity_ShouldRunDeviationRangeValidation_WhenDeviationRangesArePresent() {
        TechnologyProperties properties = new TechnologyProperties();
        var deviationRange1 = mock(DeviationRange.class);
        var deviationRange2 = mock(DeviationRange.class);
        properties.setDeviationRanges(new ArrayList<>(List.of(deviationRange1, deviationRange2)));
        properties.setIsPercentage(true);

        assertDoesNotThrow(() -> validator.validateEntity(dataSet, properties));

        verify(mockDeviationRangeValidator).runValidation(dataSet, deviationRange1);
        verify(mockDeviationRangeValidator).runValidation(dataSet, deviationRange2);
        verifyNoMoreInteractions(mockDeviationRangeValidator);
    }
}
