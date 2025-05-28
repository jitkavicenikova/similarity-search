package thesis.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Technology;
import thesis.data.model.TechnologyProperties;
import thesis.data.repository.ResultRepository;
import thesis.data.repository.TechnologyRepository;
import thesis.exceptions.BadRequestException;
import thesis.exceptions.EntityInUseException;
import thesis.exceptions.EntityNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TechnologyServiceTest {
    @InjectMocks
    private TechnologyService technologyService;

    @Mock
    private TechnologyRepository technologyRepository;

    @Mock
    private ResultRepository resultRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveTechnology_WhenTechnologyDoesNotExist() {
        Technology technology = new Technology("tech1");

        when(technologyRepository.existsById("tech1")).thenReturn(false);
        when(technologyRepository.save(technology)).thenReturn(technology);

        Technology savedTechnology = technologyService.save(technology);

        assertNotNull(savedTechnology);
        verify(technologyRepository).save(technology);
    }

    @Test
    void save_ShouldReturnExistingTechnology_WhenTechnologyAlreadyExists() {
        Technology existingTechnology = new Technology("tech1");

        when(technologyRepository.existsById("tech1")).thenReturn(true);
        when(technologyRepository.findById("tech1")).thenReturn(Optional.of(existingTechnology));

        Technology savedTechnology = technologyService.save(existingTechnology);

        assertEquals(existingTechnology, savedTechnology);
        verify(technologyRepository, never()).save(existingTechnology);
    }

    @Test
    void getEntity_ShouldReturnTechnology_WhenTechnologyExists() {
        Technology technology = new Technology("tech1");

        when(technologyRepository.findById("tech1")).thenReturn(Optional.of(technology));

        Technology result = technologyService.getEntity("tech1");
        assertEquals("tech1", result.getName());
    }

    @Test
    void getEntity_ShouldThrowEntityNotFoundException_WhenTechnologyDoesNotExist() {
        when(technologyRepository.findById("tech1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> technologyService.getEntity("tech1"));
    }

    @Test
    void delete_ShouldDeleteTechnology_WhenTechnologyIsNotUsed() {
        Technology technology = new Technology("tech1");

        when(technologyRepository.findById("tech1")).thenReturn(Optional.of(technology));
        when(resultRepository.existsByTechnologyName("tech1")).thenReturn(false);

        technologyService.delete("tech1");
        verify(technologyRepository).deleteById("tech1");
    }

    @Test
    void delete_ShouldThrowEntityInUseException_WhenTechnologyIsUsedInResults() {
        String technologyName = "tech1";
        Technology technology = new Technology(technologyName);

        when(technologyRepository.findById(technologyName)).thenReturn(Optional.of(technology));
        when(resultRepository.existsByTechnologyName(technologyName)).thenReturn(true);

        EntityInUseException exception = assertThrows(EntityInUseException.class,
                () -> technologyService.delete(technologyName));

        assertEquals("Cannot delete technology with name tech1 because it is used in results", exception.getMessage());
        verify(technologyRepository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldThrowEntityInUseException_WhenTechnologyIsUsedInComparableWith() {
        String technologyName = "tech1";
        TechnologyProperties techProperties = new TechnologyProperties();
        techProperties.setComparableWith(List.of("tech1"));

        Technology otherTechnology = new Technology("tech2");
        otherTechnology.setProperties(List.of(techProperties));

        when(technologyRepository.findById(technologyName)).thenReturn(Optional.of(new Technology(technologyName)));
        when(technologyRepository.findAll()).thenReturn(List.of(otherTechnology));

        EntityInUseException exception = assertThrows(EntityInUseException.class,
                () -> technologyService.delete(technologyName));

        assertEquals("Cannot delete technology with name tech1 because it is used in comparableWith", exception.getMessage());
        verify(technologyRepository, never()).deleteById(technologyName);
    }


    @Test
    void addProperties_ShouldAddProperties_WhenMarkerDoesNotExist() {
        Technology technology = new Technology("tech1");
        TechnologyProperties newProperties = new TechnologyProperties("marker1", false, null, null);

        when(technologyRepository.findById("tech1")).thenReturn(Optional.of(technology));
        when(technologyRepository.save(technology)).thenReturn(technology);

        Technology result = technologyService.addProperties("tech1", newProperties);

        assertNotNull(result.getProperties());
        assertEquals(1, result.getProperties().size());
        assertEquals("marker1", result.getProperties().get(0).getMarkerName());
    }

    @Test
    void addProperties_ShouldThrowBadRequestException_WhenMarkerAlreadyExists() {
        Technology technology = new Technology("tech1");
        TechnologyProperties existingProperties = new TechnologyProperties("marker1", false, null, null);
        technology.setProperties(List.of(existingProperties));

        when(technologyRepository.findById("tech1")).thenReturn(Optional.of(technology));

        TechnologyProperties newProperties = new TechnologyProperties("marker1", false, null, null);

        assertThrows(BadRequestException.class, () -> technologyService.addProperties("tech1", newProperties));
    }

    @Test
    void findAll_ShouldReturnAllTechnologies() {
        List<Technology> technologies = List.of(new Technology("tech1"), new Technology("tech2"));
        when(technologyRepository.findAll()).thenReturn(technologies);

        Iterable<Technology> result = technologyService.findAll();

        assertNotNull(result);
        assertEquals(2, ((Collection<?>) result).size());
        verify(technologyRepository).findAll();
    }
}
