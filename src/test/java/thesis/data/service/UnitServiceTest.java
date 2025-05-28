package thesis.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.Conversion;
import thesis.data.model.Unit;
import thesis.data.repository.MarkerRepository;
import thesis.data.repository.UnitRepository;
import thesis.exceptions.BadRequestException;
import thesis.exceptions.EntityInUseException;
import thesis.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UnitServiceTest {
    @Mock
    private UnitRepository unitRepository;

    @Mock
    private MarkerRepository markerRepository;

    @InjectMocks
    private UnitService unitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveUnit_WhenUnitDoesNotExist() {
        Unit unit = new Unit("unit1", "u1");
        when(unitRepository.existsById("unit1")).thenReturn(false);
        when(unitRepository.save(unit)).thenReturn(unit);

        Unit savedUnit = unitService.save(unit);

        assertEquals(unit, savedUnit);
        verify(unitRepository).save(unit);
    }

    @Test
    void save_ShouldReturnExistingUnit_WhenUnitAlreadyExists() {
        Unit unit = new Unit("unit1", "u1");
        when(unitRepository.existsById("unit1")).thenReturn(true);
        when(unitRepository.findById("unit1")).thenReturn(Optional.of(unit));

        Unit existingUnit = unitService.save(unit);

        assertEquals(unit, existingUnit);
        verify(unitRepository, never()).save(unit);
    }

    @Test
    void getEntity_ShouldReturnUnit_WhenUnitExists() {
        Unit unit = new Unit("unit1", "u1");
        when(unitRepository.findById("unit1")).thenReturn(Optional.of(unit));

        Unit retrievedUnit = unitService.getEntity("unit1");

        assertEquals(unit, retrievedUnit);
    }

    @Test
    void getEntity_ShouldThrowEntityNotFoundException_WhenUnitDoesNotExist() {
        when(unitRepository.findById("unit1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> unitService.getEntity("unit1"));
    }

    @Test
    void delete_ShouldThrowEntityNotFoundException_WhenUnitDoesNotExist() {
        when(unitRepository.existsById("unit1")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> unitService.delete("unit1"));
    }

    @Test
    void delete_ShouldThrowEntityInUseException_WhenUnitIsUsedInMarkers() {
        when(unitRepository.existsById("unit1")).thenReturn(true);
        when(markerRepository.existsByUnitName("unit1")).thenReturn(true);

        assertThrows(EntityInUseException.class, () -> unitService.delete("unit1"));
        verify(unitRepository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldThrowEntityInUseException_WhenUnitIsUsedInConversions() {
        Unit unit2 = new Unit("unit2", "u2");
        unit2.setConversions(List.of(new Conversion("unit1", "marker1", "x+1")));

        when(unitRepository.existsById("unit1")).thenReturn(true);
        when(markerRepository.existsByUnitName("unit1")).thenReturn(false);
        when(unitRepository.findAll()).thenReturn(List.of(unit2));

        assertThrows(EntityInUseException.class, () -> unitService.delete("unit1"));
        verify(unitRepository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldDeleteUnit_WhenUnitIsNotUsed() {
        when(unitRepository.existsById("unit1")).thenReturn(true);
        when(markerRepository.existsByUnitName("unit1")).thenReturn(false);
        when(unitRepository.findAll()).thenReturn(List.of());

        unitService.delete("unit1");

        verify(unitRepository).deleteById("unit1");
    }

    @Test
    void addConversion_ShouldThrowBadRequestException_WhenSourceAndTargetUnitsAreSame() {
        Conversion conversion = new Conversion("unit1", "marker1", "x+1");

        assertThrows(BadRequestException.class, () -> unitService.addConversion("unit1", conversion));
    }

    @Test
    void addConversion_ShouldThrowBadRequestException_WhenConversionAlreadyExists() {
        Conversion existingConversion = new Conversion("unit2", "marker1", "x+1");
        Unit unit = new Unit("unit1", "u1");
        unit.setConversions(List.of(existingConversion));

        when(unitRepository.findById("unit1")).thenReturn(Optional.of(unit));

        Conversion newConversion = new Conversion("unit2", "marker1", "x+1");

        assertThrows(BadRequestException.class, () -> unitService.addConversion("unit1", newConversion));
    }

    @Test
    void findAll_ShouldReturnAllUnits() {
        List<Unit> units = List.of(new Unit("unit1", "u1"), new Unit("unit2", "u2"));
        when(unitRepository.findAll()).thenReturn(units);

        Iterable<Unit> result = unitService.findAll();

        assertIterableEquals(units, result);
    }
}
