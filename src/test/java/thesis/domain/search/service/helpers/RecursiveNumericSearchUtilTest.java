package thesis.domain.search.service.helpers;

import org.junit.jupiter.api.Test;
import thesis.data.enums.AggregationType;
import thesis.domain.search.dto.AggregatedResult;
import thesis.domain.search.dto.NumericSearchConfiguration;
import thesis.domain.search.dto.RecursiveResult;
import thesis.domain.search.dto.enums.NumericSearchType;
import thesis.exceptions.BadRequestException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecursiveNumericSearchUtilTest {
    @Test
    void aggregateResults_ShouldAggregateUsingSum_WhenAggregationTypeIsSum() {
        String recordId = "rec1";
        var recursiveResult1 = new RecursiveResult("rec1", 2.0, 4.0);
        recursiveResult1.setMarkerName("marker1");
        recursiveResult1.setResultId("result1");
        var recursiveResult2 = new RecursiveResult("rec1", 3.0, 5.0);
        recursiveResult2.setMarkerName("marker2");
        recursiveResult2.setResultId("result2");
        List<RecursiveResult> recursiveResults = List.of(recursiveResult1, recursiveResult2);

        AggregatedResult aggregatedResult = RecursiveNumericSearchUtil.aggregateResults(recordId, recursiveResults, AggregationType.SUM);

        assertThat(aggregatedResult.getRecordId()).isEqualTo("rec1");
        assertThat(aggregatedResult.getMin()).isEqualTo(5.0); // 2.0 + 3.0
        assertThat(aggregatedResult.getMax()).isEqualTo(9.0); // 4.0 + 5.0
        assertThat(aggregatedResult.getMarkerNames()).containsExactly("marker1", "marker2");
        assertThat(aggregatedResult.getResultIds()).containsExactly("result1", "result2");
    }

    @Test
    void aggregateResults_ShouldAggregateUsingAverage_WhenAggregationTypeIsAverage() {
        String recordId = "rec1";
        var recursiveResult1 = new RecursiveResult("rec1", 2.0, 4.0);
        recursiveResult1.setMarkerName("marker1");
        recursiveResult1.setResultId("result1");
        var recursiveResult2 = new RecursiveResult("rec1", 3.0, 5.0);
        recursiveResult2.setMarkerName("marker2");
        recursiveResult2.setResultId("result2");
        List<RecursiveResult> recursiveResults = List.of(recursiveResult1, recursiveResult2);

        AggregatedResult aggregatedResult = RecursiveNumericSearchUtil.aggregateResults(recordId, recursiveResults, AggregationType.AVERAGE);

        assertThat(aggregatedResult.getRecordId()).isEqualTo("rec1");
        assertThat(aggregatedResult.getMin()).isEqualTo(2.5); // (2.0 + 3.0) / 2
        assertThat(aggregatedResult.getMax()).isEqualTo(4.5); // (4.0 + 5.0) / 2
        assertThat(aggregatedResult.getMarkerNames()).containsExactly("marker1", "marker2");
        assertThat(aggregatedResult.getResultIds()).containsExactly("result1", "result2");
    }

    @Test
    void aggregateResults_ShouldHandleEmptyList() {
        String recordId = "rec1";
        List<RecursiveResult> recursiveResults = List.of();

        AggregatedResult aggregatedResult = RecursiveNumericSearchUtil.aggregateResults(recordId, recursiveResults, AggregationType.SUM);

        assertThat(aggregatedResult.getRecordId()).isEqualTo("rec1");
        assertThat(aggregatedResult.getMin()).isEqualTo(0.0); // Sum of empty list should be 0
        assertThat(aggregatedResult.getMax()).isEqualTo(0.0); // Sum of empty list should be 0
        assertThat(aggregatedResult.getMarkerNames()).isEmpty();
        assertThat(aggregatedResult.getResultIds()).isEmpty();
    }

    @Test
    void filterByNumericSearchType_WhenExactMatchWithoutTolerance() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 10.0, 20.0, null, null, NumericSearchType.EXACT_MATCH, false);
        config.setWithTolerance(false);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 12.0, 18.0)).isTrue();  // In range
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, 20.0)).isTrue();  // Boundary values
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 5.0, 25.0)).isFalse();  // Out of range
    }

    @Test
    void filterByNumericSearchType_WhenExactMatchWithTolerance() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 10.0, 20.0, null, null, NumericSearchType.EXACT_MATCH, true);
        config.setWithTolerance(true);

        assertThrows(BadRequestException.class, () -> RecursiveNumericSearchUtil.filterByNumericSearchType(config, 12.0, 18.0));
    }

    @Test
    void filterByNumericSearchType_WhenMinOutOfRangeWithoutTolerance() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 10.0, 20.0, null, null, NumericSearchType.MIN_OUT_OF_RANGE, false);
        config.setWithTolerance(false);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 5.0, 15.0)).isTrue();   // min < config.getMinimum()
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 5.0, 22.0)).isFalse();   // Both min and max out of range
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 12.0, 18.0)).isFalse();  // Within range
    }

    @Test
    void filterByNumericSearchType_WhenMaxOutOfRangeWithoutTolerance() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 10.0, 20.0, null, null, NumericSearchType.MAX_OUT_OF_RANGE, false);
        config.setWithTolerance(false);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 15.0, 25.0)).isTrue();  // max > config.getMaximum()
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 5.0, 25.0)).isFalse();  // Both min and max out of range
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, 20.0)).isFalse();  // Exact match
    }

    @Test
    void filterByNumericSearchType_WhenBothOutOfRangeWithoutTolerance() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 10.0, 20.0, null, null, NumericSearchType.BOTH_OUT_OF_RANGE, false);
        config.setWithTolerance(false);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 5.0, 25.0)).isTrue();   // Both min and max out of range
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, 20.0)).isFalse();   // Both on boundary
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 5.0, 18.0)).isFalse();  // Only min is out of range
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 12.0, 25.0)).isFalse();  // Only max is out of range
    }

    @Test
    void filterByNumericSearchType_ShouldHandleNaNValues() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 10.0, 20.0, null, null, NumericSearchType.EXACT_MATCH, false);
        config.setWithTolerance(false);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, Double.NaN, 18.0)).isFalse();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 12.0, Double.NaN)).isFalse();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, Double.NaN, Double.NaN)).isFalse();
    }

    @Test
    void filterByNumericSearchType_WhenMinOutOfRangeWithTolerance() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 10.0, 20.0, 8.0, null, NumericSearchType.MIN_OUT_OF_RANGE, true);
        config.setWithTolerance(true);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 8.5, 15.0)).isTrue();   // min within tolerance range
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, 15.0)).isFalse();   // min on boundary
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 7.5, 15.0)).isFalse();  // min < tolerance
    }

    @Test
    void filterByNumericSearchType_WhenMaxOutOfRangeWithTolerance() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 10.0, 20.0, null, 22.0, NumericSearchType.MAX_OUT_OF_RANGE, true);
        config.setWithTolerance(true);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 12.0, 21.5)).isTrue();   // max within tolerance range
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 12.0, 20.0)).isFalse();   // max on boundary
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 12.0, 23.0)).isFalse();  // max > tolerance
    }

    @Test
    void filterByNumericSearchType_WhenBothOutOfRangeWithTolerance() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 10.0, 20.0, 8.0, 22.0, NumericSearchType.BOTH_OUT_OF_RANGE, true);
        config.setWithTolerance(true);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 8.5, 21.5)).isTrue();   // Within tolerance for both
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, 20.0)).isFalse();   // On boundary
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 7.5, 21.5)).isFalse();  // min out of tolerance
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 8.5, 23.0)).isFalse();  // max out of tolerance
    }

    @Test
    void filterByNumericSearchType_WhenMinIsNull_ForExactMatch() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", Double.NEGATIVE_INFINITY, 20.0, null, null, NumericSearchType.EXACT_MATCH, false);
        config.setWithTolerance(false);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, null, 20.0)).isTrue();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, 20.0)).isTrue();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, null)).isFalse();
    }

    @Test
    void filterByNumericSearchType_WhenMaxIsNull_ForExactMatch() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 10.0, Double.POSITIVE_INFINITY, null, null, NumericSearchType.EXACT_MATCH, false);
        config.setWithTolerance(false);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, null, 20.0)).isFalse();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, 20.0)).isTrue();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, null)).isTrue();
    }

    @Test
    void filterByNumericSearchType_WhenMinIsNull_ForMaxOFR() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", Double.NEGATIVE_INFINITY, 20.0, null, null, NumericSearchType.MAX_OUT_OF_RANGE, false);
        config.setWithTolerance(false);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, null, 25.0)).isTrue();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, 20.0)).isFalse();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 20.0, 21.0)).isTrue();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, null)).isTrue();
    }

    @Test
    void filterByNumericSearchType_WhenMaxIsNull_ForMinOFR() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 7.0, Double.POSITIVE_INFINITY, null, null, NumericSearchType.MIN_OUT_OF_RANGE, false);
        config.setWithTolerance(false);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 5.0, 10.0)).isTrue();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 6.0, null)).isTrue();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, null, 25.0)).isTrue();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 8.0, 12.0)).isFalse();
    }

    @Test
    void filterByNumericSearchType_WhenMinIsNull_ForMaxOFRWithTolerance() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", Double.NEGATIVE_INFINITY, 20.0, null, 22.0, NumericSearchType.MAX_OUT_OF_RANGE, false);
        config.setWithTolerance(true);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, null, 25.0)).isFalse();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, 20.0)).isFalse();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 20.0, 21.0)).isTrue();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 10.0, null)).isFalse();
    }

    @Test
    void filterByNumericSearchType_WhenMaxIsNull_ForMinOFRWithTolerance() {
        NumericSearchConfiguration config = new NumericSearchConfiguration("marker", "unit", 7.0, Double.POSITIVE_INFINITY, 6.0, null, NumericSearchType.MIN_OUT_OF_RANGE, false);
        config.setWithTolerance(true);

        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 5.0, 10.0)).isFalse();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 6.0, null)).isTrue();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, null, 25.0)).isFalse();
        assertThat(RecursiveNumericSearchUtil.filterByNumericSearchType(config, 8.0, 12.0)).isFalse();
    }
}
