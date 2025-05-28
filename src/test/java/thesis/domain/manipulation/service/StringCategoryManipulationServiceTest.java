package thesis.domain.manipulation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.model.StringCategory;
import thesis.data.service.StringCategoryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StringCategoryManipulationServiceTest {
    @Mock
    private StringCategoryService stringCategoryService;

    @InjectMocks
    private StringCategoryManipulationService stringCategoryManipulationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addValue_ShouldReturnUpdatedStringCategory_WhenValueIsAddedSuccessfully() {
        String categoryName = "category1";
        String value = "newValue";

        StringCategory updatedCategory = new StringCategory();
        updatedCategory.setName(categoryName);
        updatedCategory.setValues(List.of(value));

        when(stringCategoryService.addValue(categoryName, value)).thenReturn(updatedCategory);

        StringCategory result = stringCategoryManipulationService.addValue(categoryName, value);

        assertNotNull(result);
        assertEquals(categoryName, result.getName());
        assertEquals(1, result.getValues().size());
        assertTrue(result.getValues().contains(value));

        verify(stringCategoryService).addValue(categoryName, value);
    }
}
