package thesis.data.validation.base;

import org.junit.jupiter.api.Test;
import thesis.data.enums.AggregationType;
import thesis.data.model.Marker;
import thesis.exceptions.ValidationException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MarkerBaseValidatorTest {
    private final MarkerBaseValidator validator = new MarkerBaseValidator() {
    };

    @Test
    void validate_ShouldThrowException_WhenAggregationTypeIsSetWithoutChildMarkers() {
        var marker = new Marker();
        marker.setAggregationType(AggregationType.SUM);

        assertThrows(ValidationException.class, () -> validator.validate(marker),
                "Expected ValidationException when aggregation type is set without child markers");
    }

    @Test
    void validate_ShouldThrowException_WhenMarkerListsItselfAsAChild() {
        var marker = new Marker();
        marker.setName("Marker1");
        marker.setChildMarkerNames(List.of("Marker1"));

        assertThrows(ValidationException.class, () -> validator.validate(marker),
                "Expected ValidationException when marker lists itself as a child marker");
    }

    @Test
    void validate_ShouldThrowException_WhenChildMarkersAreSetWithoutAggregationType() {
        var marker = new Marker();
        marker.setName("Marker1");
        marker.setChildMarkerNames(List.of("ChildMarker"));

        assertThrows(ValidationException.class, () -> validator.validate(marker),
                "Expected ValidationException when child markers are set without aggregation type");
    }

    @Test
    void validate_ShouldThrowException_WhenDuplicateChildMarkerNamesArePresent() {
        var marker = new Marker();
        marker.setAggregationType(AggregationType.SUM);
        marker.setChildMarkerNames(Arrays.asList("Child1", "Child2", "Child1"));

        assertThrows(ValidationException.class, () -> validator.validate(marker),
                "Expected ValidationException when duplicate child marker names are found");
    }

    @Test
    void validate_ShouldThrowException_WhenChildMarkerNamesContainNullOrEmptyValues() {
        var marker = new Marker();
        marker.setName("Marker1");
        marker.setAggregationType(AggregationType.SUM);
        marker.setChildMarkerNames(Arrays.asList("Child1", "", null));

        assertThrows(ValidationException.class, () -> validator.validate(marker));
    }

    @Test
    void validate_ShouldPass_ForValidMarker() {
        var marker = new Marker();
        marker.setName("Marker1");
        marker.setAggregationType(AggregationType.SUM);
        marker.setChildMarkerNames(List.of("Child1", "Child2"));

        validator.validate(marker);
    }
}
