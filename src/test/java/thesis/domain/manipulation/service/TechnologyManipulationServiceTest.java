package thesis.domain.manipulation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Technology;
import thesis.data.model.TechnologyProperties;
import thesis.data.service.TechnologyService;
import thesis.data.validation.database.TechnologyPropertiesDatabaseValidator;
import thesis.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TechnologyManipulationServiceTest {
    @Mock
    private TechnologyService technologyService;

    @Mock
    private TechnologyPropertiesDatabaseValidator propertiesDatabaseValidator;

    @InjectMocks
    private TechnologyManipulationService technologyManipulationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProperties_ShouldReturnUpdatedTechnology_WhenPropertiesAreAddedSuccessfully() {
        String technologyName = "TechA";
        TechnologyProperties technologyProperties = new TechnologyProperties();

        Technology updatedTechnology = new Technology();
        updatedTechnology.setName(technologyName);

        doNothing().when(propertiesDatabaseValidator).runValidation(technologyProperties);
        when(technologyService.addProperties(technologyName, technologyProperties)).thenReturn(updatedTechnology);

        Technology result = technologyManipulationService.addProperties(technologyName, technologyProperties);

        assertNotNull(result);
        assertEquals(technologyName, result.getName());

        verify(propertiesDatabaseValidator).runValidation(technologyProperties);
        verify(technologyService).addProperties(technologyName, technologyProperties);
    }

    @Test
    void addProperties_ShouldThrowValidationException_WhenValidationFails() {
        String technologyName = "TechA";
        TechnologyProperties technologyProperties = new TechnologyProperties();

        doThrow(new ValidationException("Invalid properties")).when(propertiesDatabaseValidator).runValidation(technologyProperties);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> technologyManipulationService.addProperties(technologyName, technologyProperties)
        );

        assertEquals("Invalid properties", exception.getMessage());

        verify(propertiesDatabaseValidator).runValidation(technologyProperties);
        verifyNoInteractions(technologyService);
    }
}

