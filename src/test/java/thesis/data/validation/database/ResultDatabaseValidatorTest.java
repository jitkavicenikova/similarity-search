package thesis.data.validation.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.enums.AggregationType;
import thesis.data.model.Marker;
import thesis.data.model.Result;
import thesis.data.model.StringCategory;
import thesis.data.service.MarkerService;
import thesis.data.service.StringCategoryService;
import thesis.data.service.TechnologyService;
import thesis.data.service.UnitService;
import thesis.exceptions.EntityNotFoundException;
import thesis.exceptions.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ResultDatabaseValidatorTest {
    @InjectMocks
    private ResultDatabaseValidator validator;

    @Mock
    private MarkerService markerService;

    @Mock
    private UnitService unitService;

    @Mock
    private TechnologyService technologyService;

    @Mock
    private StringCategoryService stringCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateEntity_ShouldThrowException_WhenMarkerHasAggregationType() {
        Result result = new Result();
        result.setMin(1.0);
        result.setMarkerName("markerWithAggregationType");

        var marker = new Marker();
        marker.setAggregationType(AggregationType.SUM);

        when(markerService.getEntity("markerWithAggregationType")).thenReturn(marker);

        assertThrows(ValidationException.class, () -> validator.validateEntity(result),
                "Marker with children cannot be used in result");

        verify(markerService).getEntity("markerWithAggregationType");
    }

    @Test
    void validateEntity_ShouldThrowException_WhenUnitRawDoesNotExist() {
        Result result = new Result();
        result.setMin(1.0);
        result.setUnitRawName("nonExistentUnitRaw");

        when(markerService.getEntity(any())).thenReturn(new Marker());
        when(unitService.existsById("nonExistentUnitRaw")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> validator.validateEntity(result),
                "Unit raw with name nonExistentUnitRaw does not exist");

        verify(unitService).existsById("nonExistentUnitRaw");
    }

    @Test
    void validateEntity_ShouldThrowException_WhenTechnologyDoesNotExist() {
        Result result = new Result();
        result.setMin(1.0);
        result.setTechnologyName("nonExistentTechnology");

        when(markerService.getEntity(any())).thenReturn(new Marker());
        when(technologyService.existsById("nonExistentTechnology")).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> validator.validateEntity(result),
                "Technology with name nonExistentTechnology does not exist");

        verify(technologyService).existsById("nonExistentTechnology");
    }

    @Test
    void validateEntity_ShouldThrowException_WhenStringValueIsNotInCategory() {
        Result result = new Result();
        result.setStringValueCategory("someCategory");
        result.setStringValue("invalidValue");

        StringCategory category = new StringCategory();
        category.setValues(List.of("validValue1", "validValue2"));

        when(markerService.getEntity(any())).thenReturn(new Marker());
        when(stringCategoryService.getEntity("someCategory")).thenReturn(category);

        assertThrows(ValidationException.class, () -> validator.validateEntity(result),
                "Value invalidValue is not in category someCategory");

        verify(stringCategoryService).getEntity("someCategory");
    }

    @Test
    void validateEntity_ShouldPass_WhenAllChecksSucceed() {
        Result result = new Result();
        result.setMarkerName("validMarker");
        result.setUnitRawName("validUnitRaw");
        result.setTechnologyName("validTechnology");
        result.setStringValueCategory("validCategory");
        result.setStringValue("validValue");

        var marker = new thesis.data.model.Marker();
        marker.setUnitName("validUnit");
        StringCategory category = new StringCategory();
        category.setValues(List.of("validValue"));

        when(markerService.getEntity("validMarker")).thenReturn(marker);
        when(unitService.existsById("validUnitRaw")).thenReturn(true);
        when(technologyService.existsById("validTechnology")).thenReturn(true);
        when(stringCategoryService.getEntity("validCategory")).thenReturn(category);

        validator.validateEntity(result);

        verify(markerService).getEntity("validMarker");
        verify(unitService).existsById("validUnitRaw");
        verify(technologyService).existsById("validTechnology");
        verify(stringCategoryService).getEntity("validCategory");
    }
}
