package thesis.domain.manipulation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.service.EntityService;
import thesis.data.validation.database.DatabaseValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BaseEntityManipulationServiceTest {
    private final TestEntity testEntity = new TestEntity("test-id");
    private BaseEntityManipulationService<TestEntity> service;
    @Mock
    private EntityService<TestEntity> entityService;
    @Mock
    private DatabaseValidator<TestEntity> databaseValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new BaseEntityManipulationService<>(entityService, databaseValidator) {
            // No extra implementation needed for abstract class test
        };
    }

    @Test
    void save_ShouldSaveEntity_WhenEntityDoesNotExist() {
        when(entityService.existsById(testEntity.getId())).thenReturn(false);
        when(entityService.save(testEntity)).thenReturn(testEntity);

        TestEntity result = service.save(testEntity);

        verify(databaseValidator).runValidation(testEntity);
        verify(entityService).save(testEntity);
        assertEquals(testEntity, result);
    }

    @Test
    void save_ShouldThrowException_WhenEntityAlreadyExists() {
        when(entityService.existsById(testEntity.getId())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.save(testEntity));

        assertEquals("Entity with id 'test-id' already exists", exception.getMessage());
        verify(databaseValidator, never()).runValidation(any());
        verify(entityService, never()).save(any());
    }

    @Test
    void getEntity_ShouldDelegateToService() {
        when(entityService.getEntity("test-id")).thenReturn(testEntity);

        TestEntity result = service.getEntity("test-id");

        assertEquals(testEntity, result);
        verify(entityService).getEntity("test-id");
    }

    @Test
    void delete_ShouldDelegateToService() {
        service.delete("test-id");

        verify(entityService).delete("test-id");
    }

    @Test
    void findAll_ShouldDelegateToService() {
        Iterable<TestEntity> expected = List.of(testEntity);
        when(entityService.findAll()).thenReturn(expected);

        Iterable<TestEntity> result = service.findAll();

        assertEquals(expected, result);
        verify(entityService).findAll();
    }
}
