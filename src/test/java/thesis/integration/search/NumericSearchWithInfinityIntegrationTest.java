package thesis.integration.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import thesis.RedisContainerTestBase;
import thesis.data.enums.AggregationType;
import thesis.data.model.Marker;
import thesis.data.model.Result;
import thesis.data.model.Unit;
import thesis.data.service.MarkerService;
import thesis.data.service.ResultService;
import thesis.data.service.UnitService;
import thesis.domain.search.dto.AggregatedResult;
import thesis.domain.search.dto.NumericSearchOptions;
import thesis.domain.search.dto.enums.NumericSearchType;
import thesis.domain.search.service.NumericSearchService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("soil")
public class NumericSearchWithInfinityIntegrationTest extends RedisContainerTestBase {
    @Autowired
    private NumericSearchService numericSearchService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private MarkerService markerService;

    @Autowired
    private UnitService unitService;

    @DynamicPropertySource
    static void setRedisProperties(DynamicPropertyRegistry registry) {
        redisContainer.start();
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    void setUpData() {
        var result1 = new Result(1, "Marker1", 5.0, 7.0, null, null, null, "Sample1", "Tech1", null, null, null, null);
        result1.setRecordId("1");
        result1.setId("1:Marker1");
        var result2 = new Result(2, "Marker1", 6.0, 6.0, null, null, null, "Sample1", "Tech2", null, null, null, null);
        result2.setRecordId("2");
        result2.setId("2:Marker1");
        var result3 = new Result(3, "Marker1", null, 6.0, null, null, null, null, "Tech3", null, null, null, null);
        result3.setRecordId("3");
        result3.setId("3:Marker1");
        var result4 = new Result(4, "Marker1", 5.0, null, null, null, null, null, "Tech1", null, null, null, null);
        result4.setRecordId("4");
        result4.setId("4:Marker1");
        var result5 = new Result(5, "Marker1", null, 10.0, null, null, null, null, "Tech1", null, null, null, null);
        result5.setRecordId("5");
        result5.setId("5:Marker1");
        var result6 = new Result(6, "Marker1", 3.0, null, null, null, null, "Sample1", "Tech1", null, null, null, null);
        result6.setRecordId("6");
        result6.setId("6:Marker1");
        resultService.save(result1);
        resultService.save(result2);
        resultService.save(result3);
        resultService.save(result4);
        resultService.save(result5);
        resultService.save(result6);

        markerService.save(new Marker("Marker1", null, null, "Unit1"));

        unitService.save(new Unit("Unit1", "U1"));
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMax() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(3);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2, 4);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMin() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMaximum(7.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(3);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMinMaxOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMaximum(7.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(3);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(4, 5, 6);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMaxMinOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(3);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(3, 5, 6);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRangeMinOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setMaximum(7.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(3);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRangeMaxOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setMaximum(7.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(4);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRangeBothOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setMaximum(7.0);
        options.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(2);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(5, 6);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMinMaxOFRWithAbsTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMaximum(7.0);
        options.setAbsoluteTolerance(3.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(5);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMaxMinOFRWithAbsTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setAbsoluteTolerance(3.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(6);
    }

    @Test
    void processRecursiveNumericSearch_ShouldReturnResults_WhenSearchingWithoutMax() {
        saveRecursiveData();
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(5.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(3);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("1", "2", "4");
    }

    @Test
    void processRecursiveNumericSearch_ShouldReturnResults_WhenSearchingWithoutMin() {
        saveRecursiveData();
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMaximum(15.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(3);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("1", "2", "3");
    }

    @Test
    void processRecursiveNumericSearch_ShouldReturnResults_WhenSearchingWithoutMaxMinOFR() {
        saveRecursiveData();
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(8.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(4);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("3", "4", "5", "6");
    }

    @Test
    void processRecursiveNumericSearch_ShouldReturnResults_WhenSearchingWithoutMinMaxOFR() {
        saveRecursiveData();
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMaximum(15.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(3);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("4", "5", "6");
    }

    @Test
    void processRecursiveNumericSearch_ShouldReturnResults_WhenSearchingWithoutMinBothOFR() {
        saveRecursiveData();
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(8.0);
        options.setMaximum(12.0);
        options.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(4);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("3", "4", "5", "6");
    }

    @Test
    void processRecursiveNumericSearch_ShouldReturnResults_WhenSearchingWithoutMaxMinOFRWithTolerance() {
        saveRecursiveData();
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(8.0);
        options.setAbsoluteTolerance(3.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(1);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("4");
    }

    @Test
    void processRecursiveNumericSearch_ShouldReturnResults_WhenSearchingWithoutMinMaxOFRWithTolerance() {
        saveRecursiveData();
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMaximum(15.0);
        options.setAbsoluteTolerance(3.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(1);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("5");
    }

    @Test
    void processRecursiveNumericSearch_ShouldReturnResults_WhenSearchingWithoutMinBothOFRWithTolerance() {
        saveRecursiveData();
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(8.0);
        options.setMaximum(12.0);
        options.setAbsoluteTolerance(3.0);
        options.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).isEmpty();
    }

    private void saveRecursiveData() {
        var result12 = new Result(1, "Marker2", 5.0, 5.0, null, null, null, "Sample1", "Tech1", null, null, null, null);
        result12.setRecordId("1");
        result12.setId("1:Marker2");
        var result22 = new Result(2, "Marker2", 2.0, 6.0, null, null, null, "Sample1", "Tech2", null, null, null, null);
        result22.setRecordId("2");
        result22.setId("2:Marker2");
        var result32 = new Result(3, "Marker2", 4.0, 9.0, null, null, null, null, "Tech3", null, null, null, null);
        result32.setRecordId("3");
        result32.setId("3:Marker2");
        var result42 = new Result(4, "Marker2", 2.0, 7.0, null, null, null, null, "Tech3", null, null, null, null);
        result42.setRecordId("4");
        result42.setId("4:Marker2");
        var result52 = new Result(5, "Marker2", 2.0, 7.0, null, null, null, null, "Tech3", null, null, null, null);
        result52.setRecordId("5");
        result52.setId("5:Marker2");
        var result62 = new Result(6, "Marker2", null, 7.0, null, null, null, null, "Tech3", null, null, null, null);
        result62.setRecordId("6");
        result62.setId("6:Marker2");

        resultService.save(result12);
        resultService.save(result22);
        resultService.save(result32);
        resultService.save(result42);
        resultService.save(result52);
        resultService.save(result62);

        markerService.save(new Marker("Marker2", null, null, "Unit1"));
        var parentMarker = new Marker("ParentMarker", null, null, "Unit1");
        parentMarker.setChildMarkerNames(List.of("Marker1", "Marker2"));
        parentMarker.setAggregationType(AggregationType.SUM);

        markerService.save(parentMarker);
    }
}
