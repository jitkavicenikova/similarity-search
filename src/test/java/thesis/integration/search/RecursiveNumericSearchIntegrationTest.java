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
import thesis.data.model.Conversion;
import thesis.data.model.Marker;
import thesis.data.model.Result;
import thesis.data.model.Unit;
import thesis.data.service.MarkerService;
import thesis.data.service.ResultService;
import thesis.data.service.UnitService;
import thesis.domain.search.dto.AggregatedResult;
import thesis.domain.search.dto.NumericSearchOptions;
import thesis.domain.search.dto.SearchFilters;
import thesis.domain.search.dto.enums.NumericSearchType;
import thesis.domain.search.service.NumericSearchService;
import thesis.exceptions.BadRequestException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("soil")
public class RecursiveNumericSearchIntegrationTest extends RedisContainerTestBase {
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
    void setUp() {
        // results for Marker2
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

        // results for Marker3
        var result13 = new Result(1, "Marker3", 0.5, 0.6, null, null, null, null, "Tech1", null, null, null, null);
        result13.setRecordId("1");
        result13.setId("1:Marker3");
        var result23 = new Result(2, "Marker3", 0.2, 0.4, null, null, null, null, "Tech1", null, null, null, null);
        result23.setRecordId("2");
        result23.setId("2:Marker3");
        var result33 = new Result(3, "Marker3", 0.6, 0.8, null, null, null, "Sample1", "Tech1", null, null, null, null);
        result33.setRecordId("3");
        result33.setId("3:Marker3");
        var result43 = new Result(4, "Marker3", 0.3, 1.1, null, null, null, "Sample1", "Tech1", null, null, null, null);
        result43.setRecordId("4");
        result43.setId("4:Marker3");

        var result7 = new Result(5, "Marker2", 6.0, 6.0, null, null, null, "Sample1", "Tech1", null, null, null, null);
        result7.setRecordId("5");
        result7.setId("5:Marker2");
        resultService.save(result12);
        resultService.save(result22);
        resultService.save(result32);
        resultService.save(result42);
        resultService.save(result23);
        resultService.save(result13);
        resultService.save(result33);
        resultService.save(result43);
        resultService.save(result7);

        markerService.save(new Marker("Marker2", null, null, "Unit1"));
        markerService.save(new Marker("Marker3", null, null, "Unit2"));

        unitService.save(new Unit("Unit1", "U1"));
        var unit2 = new Unit("Unit2", "U2");
        unit2.setConversions(List.of(new Conversion("Unit1", "Marker3", "x*10")));
        unitService.save(unit2);
    }

    @Test
    void processNumericSearch_ShouldThrow_WhenSearchingWithFilters() {
        var parentMarker = createParentMarker(AggregationType.SUM);
        markerService.save(parentMarker);
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setFilters(new SearchFilters(null, null, "Sample1", null, null));
        options.setMinimum(0.5);
        options.setMaximum(0.7);

        assertThrows(BadRequestException.class, () -> numericSearchService.processNumericSearch(options));
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingValueSum() {
        var parentMarker = createParentMarker(AggregationType.SUM);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setValue(6.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(1);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("5");
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingValueAvg() {
        var parentMarker = createParentMarker(AggregationType.AVERAGE);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setValue(6.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(1);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("5");
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRangeSum() {
        var parentMarker = createParentMarker(AggregationType.SUM);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(10.0);
        options.setMaximum(12.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(1);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("1");
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRangeAvg() {
        var parentMarker = createParentMarker(AggregationType.AVERAGE);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(5.0);
        options.setMaximum(6.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(2);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("1", "5");
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRangeMinOFR() {
        var parentMarker = createParentMarker(AggregationType.AVERAGE);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(5.0);
        options.setMaximum(6.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(1);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("2");
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRangeMaxOFR() {
        var parentMarker = createParentMarker(AggregationType.SUM);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(10.0);
        options.setMaximum(12.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(1);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("3");
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRangeBothOFR() {
        var parentMarker = createParentMarker(AggregationType.SUM);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(10.0);
        options.setMaximum(12.0);
        options.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(1);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("4");
    }

    @Test
    void processNumericSearch_ShouldReturnEmpty_WhenSearchingRangeBothOFRWithLowTolerance() {
        var parentMarker = createParentMarker(AggregationType.SUM);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(10.0);
        options.setMaximum(12.0);
        options.setAbsoluteTolerance(1.0);
        options.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).isEmpty();
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRangeMinOFRWithLowTolerance() {
        var parentMarker = createParentMarker(AggregationType.SUM);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(10.0);
        options.setMaximum(12.0);
        options.setAbsoluteTolerance(10.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(1);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("2");
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMax() {
        var parentMarker = createParentMarker(AggregationType.SUM);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMinimum(10.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(2);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("1", "3");
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMin() {
        var parentMarker = createParentMarker(AggregationType.SUM);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMaximum(12.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(3);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("1", "2", "5");
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithConversions() {
        var parentMarker = createParentMarker(AggregationType.SUM);
        markerService.save(parentMarker);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("ParentMarker");
        options.setMaximum(12.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getAggregatedResults()).hasSize(3);
        assertThat(result.getAggregatedResults()).extracting(AggregatedResult::getRecordId).containsExactlyInAnyOrder("1", "2", "5");
    }

    private Marker createParentMarker(AggregationType aggregationType) {
        var parentMarker = new Marker("ParentMarker", null, null, "Unit1");
        parentMarker.setChildMarkerNames(List.of("Marker2", "Marker3"));
        parentMarker.setAggregationType(aggregationType);

        return parentMarker;
    }
}
