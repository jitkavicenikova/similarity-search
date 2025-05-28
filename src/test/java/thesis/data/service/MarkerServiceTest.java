package thesis.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.*;
import thesis.data.repository.MarkerRepository;
import thesis.data.repository.ResultRepository;
import thesis.data.repository.TechnologyRepository;
import thesis.data.repository.UnitRepository;
import thesis.exceptions.EntityInUseException;
import thesis.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarkerServiceTest {
    @Mock
    private MarkerRepository markerRepository;

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private MarkerService markerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveMarker_WhenMarkerDoesNotExist() {
        Marker marker = new Marker();
        marker.setName("marker1");

        when(markerRepository.existsById("marker1")).thenReturn(false);
        when(markerRepository.save(marker)).thenReturn(marker);

        Marker savedMarker = markerService.save(marker);

        assertNotNull(savedMarker);
        verify(markerRepository).save(marker);
    }

    @Test
    void save_ShouldReturnExistingMarker_WhenMarkerAlreadyExists() {
        Marker existingMarker = new Marker();
        existingMarker.setName("marker1");

        when(markerRepository.existsById("marker1")).thenReturn(true);
        when(markerRepository.findById("marker1")).thenReturn(Optional.of(existingMarker));

        Marker savedMarker = markerService.save(existingMarker);

        assertEquals(existingMarker, savedMarker);
        verify(markerRepository, never()).save(existingMarker);
    }

    @Test
    void getEntity_ShouldReturnMarker_WhenMarkerExists() {
        Marker marker = new Marker();
        marker.setName("marker1");

        when(markerRepository.findById("marker1")).thenReturn(Optional.of(marker));

        Marker result = markerService.getEntity("marker1");

        assertNotNull(result);
        assertEquals("marker1", result.getName());
    }

    @Test
    void getEntity_ShouldThrowException_WhenMarkerNotFound() {
        when(markerRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> markerService.getEntity("nonexistent"));
    }

    @Test
    void delete_ShouldDeleteMarker_WhenNoDependenciesExist() {
        when(markerRepository.existsById("marker1")).thenReturn(true);
        when(resultRepository.existsByMarkerName("marker1")).thenReturn(false);
        when(unitRepository.findAll()).thenReturn(List.of());
        when(technologyRepository.findAll()).thenReturn(List.of());

        markerService.delete("marker1");

        verify(markerRepository).deleteById("marker1");
    }

    @Test
    void delete_ShouldThrowException_WhenMarkerIsUsedInResults() {
        when(markerRepository.existsById("marker1")).thenReturn(true);
        when(resultRepository.existsByMarkerName("marker1")).thenReturn(true);

        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> markerService.delete("marker1"));
        assertEquals("Cannot delete marker with name 'marker1' because it is used in results", exception.getMessage());
    }

    @Test
    void delete_ShouldThrowException_WhenMarkerIsUsedInConversions() {
        when(markerRepository.existsById("marker1")).thenReturn(true);
        when(resultRepository.existsByMarkerName("marker1")).thenReturn(false);

        var unit = new Unit();
        unit.setConversions(List.of(new Conversion("unit2", "marker1", "x+1")));
        when(unitRepository.findAll()).thenReturn(List.of(unit));

        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> markerService.delete("marker1"));
        assertEquals("Cannot delete marker with name 'marker1' because it is used in conversions", exception.getMessage());
    }

    @Test
    void delete_ShouldThrowException_WhenMarkerIsUsedInTechnologyProperties() {
        when(markerRepository.existsById("marker1")).thenReturn(true);
        when(resultRepository.existsByMarkerName("marker1")).thenReturn(false);
        when(unitRepository.findAll()).thenReturn(List.of());

        var technology = new Technology();
        technology.setProperties(List.of(new TechnologyProperties("marker1", true, null, null)));
        when(technologyRepository.findAll()).thenReturn(List.of(technology));

        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> markerService.delete("marker1"));
        assertEquals("Cannot delete marker with name 'marker1' because it is used in technology properties", exception.getMessage());
    }

    @Test
    void delete_ShouldThrowException_WhenMarkerIsUsedInChildMarkers() {
        Marker parentMarker = new Marker();
        parentMarker.setChildMarkerNames(List.of("marker1"));

        when(markerRepository.existsById("marker1")).thenReturn(true);
        when(resultRepository.existsByMarkerName("marker1")).thenReturn(false);
        when(unitRepository.findAll()).thenReturn(List.of());
        when(technologyRepository.findAll()).thenReturn(List.of());
        when(markerRepository.findAll()).thenReturn(List.of(parentMarker));

        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> markerService.delete("marker1"));
        assertEquals("Cannot delete marker with name 'marker1' because it is used in child markers", exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnAllMarkers() {
        Marker marker1 = new Marker();
        marker1.setName("marker1");
        Marker marker2 = new Marker();
        marker2.setName("marker2");

        when(markerRepository.findAll()).thenReturn(List.of(marker1, marker2));

        Iterable<Marker> markers = markerService.findAll();

        assertNotNull(markers);
        assertTrue(markers.iterator().hasNext());
        verify(markerRepository).findAll();
    }
}
