package thesis.data.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.StringCategory;
import thesis.data.repository.ResultRepository;
import thesis.data.repository.StringCategoryRepository;
import thesis.exceptions.BadRequestException;
import thesis.exceptions.EntityInUseException;
import thesis.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StringCategoryServiceTest {
    @Mock
    private StringCategoryRepository stringCategoryRepository;

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private StringCategoryService stringCategoryService;

    public StringCategoryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveCategory_WhenCategoryDoesNotExist() {
        StringCategory category = new StringCategory("category1", false, List.of("value1", "value2"));
        when(stringCategoryRepository.existsById("category1")).thenReturn(false);
        when(stringCategoryRepository.save(category)).thenReturn(category);

        StringCategory savedCategory = stringCategoryService.save(category);

        assertNotNull(savedCategory);
        verify(stringCategoryRepository, times(1)).save(category);
    }

    @Test
    void save_ShouldReturnExistingCategory_WhenCategoryAlreadyExists() {
        StringCategory existingCategory = new StringCategory("category1", false, List.of("value1"));
        when(stringCategoryRepository.existsById("category1")).thenReturn(true);
        when(stringCategoryRepository.findById("category1")).thenReturn(Optional.of(existingCategory));

        StringCategory result = stringCategoryService.save(existingCategory);

        assertNotNull(result);
        assertEquals("category1", result.getName());
        verify(stringCategoryRepository, times(1)).findById("category1");
    }

    @Test
    void getEntity_ShouldReturnCategory_WhenCategoryExists() {
        StringCategory category = new StringCategory("category1", false, List.of("value1"));
        when(stringCategoryRepository.findById("category1")).thenReturn(Optional.of(category));

        StringCategory result = stringCategoryService.getEntity("category1");

        assertNotNull(result);
        assertEquals("category1", result.getName());
    }

    @Test
    void getEntity_ShouldThrowException_WhenCategoryDoesNotExist() {
        when(stringCategoryRepository.findById("category1")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> stringCategoryService.getEntity("category1"));

        assertEquals("String category with name 'category1' not found", exception.getMessage());
    }

    @Test
    void delete_ShouldDeleteCategory_WhenNotUsedInResults() {
        when(stringCategoryRepository.existsById("category1")).thenReturn(true);
        when(resultRepository.existsByStringValueCategory("category1")).thenReturn(false);

        stringCategoryService.delete("category1");

        verify(stringCategoryRepository, times(1)).deleteById("category1");
    }

    @Test
    void delete_ShouldThrowException_WhenCategoryDoesNotExist() {
        when(stringCategoryRepository.existsById("category1")).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> stringCategoryService.delete("category1"));

        assertEquals("String category with name 'category1' not found", exception.getMessage());
    }

    @Test
    void delete_ShouldThrowException_WhenCategoryIsUsedInResults() {
        when(stringCategoryRepository.existsById("category1")).thenReturn(true);
        when(resultRepository.existsByStringValueCategory("category1")).thenReturn(true);

        EntityInUseException exception = assertThrows(EntityInUseException.class,
                () -> stringCategoryService.delete("category1"));

        assertEquals("Cannot delete category 'category1' because it is used in results", exception.getMessage());
    }

    @Test
    void addValue_ShouldAddValueToCategory_WhenNotComparable() {
        StringCategory category = new StringCategory("category1", false, new ArrayList<>(List.of("value1")));

        when(stringCategoryRepository.findById("category1")).thenReturn(Optional.of(category));
        when(stringCategoryRepository.save(category)).thenReturn(category);

        StringCategory result = stringCategoryService.addValue("category1", "value2");

        assertTrue(result.getValues().contains("value2"));
    }

    @Test
    void addValue_ShouldThrowException_WhenCategoryIsComparable() {
        StringCategory category = new StringCategory("category1", true, List.of("value1"));

        when(stringCategoryRepository.findById("category1")).thenReturn(Optional.of(category));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> stringCategoryService.addValue("category1", "value2"));

        assertEquals("Cannot add value to comparable category 'category1'", exception.getMessage());
    }

    @Test
    void addValue_ShouldThrowException_WhenValueAlreadyExists() {
        StringCategory category = new StringCategory("category1", false, List.of("value1"));

        when(stringCategoryRepository.findById("category1")).thenReturn(Optional.of(category));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> stringCategoryService.addValue("category1", "value1"));

        assertEquals("Value 'value1' already exists in category 'category1'", exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnAllCategories() {
        Iterable<StringCategory> categories = mock(Iterable.class);
        when(stringCategoryRepository.findAll()).thenReturn(categories);

        Iterable<StringCategory> result = stringCategoryService.findAll();

        assertNotNull(result);
        verify(stringCategoryRepository, times(1)).findAll();
    }
}
