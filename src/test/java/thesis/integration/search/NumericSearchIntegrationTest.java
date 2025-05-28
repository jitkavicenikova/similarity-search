package thesis.integration.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import thesis.RedisContainerTestBase;
import thesis.data.model.*;
import thesis.data.service.MarkerService;
import thesis.data.service.ResultService;
import thesis.data.service.TechnologyService;
import thesis.data.service.UnitService;
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
public class NumericSearchIntegrationTest extends RedisContainerTestBase {
    @Autowired
    private NumericSearchService numericSearchService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private MarkerService markerService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private TechnologyService technologyService;

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
        var result3 = new Result(3, "Marker1", 3.0, 6.0, null, null, null, null, "Tech3", null, null, null, null);
        result3.setRecordId("3");
        result3.setId("3:Marker1");
        var result4 = new Result(4, "Marker1", 5.0, 8.0, null, null, null, null, "Tech1", null, null, null, null);
        result4.setRecordId("4");
        result4.setId("4:Marker1");
        var result5 = new Result(5, "Marker1", 2.0, 9.0, null, null, null, null, "Tech1", null, null, null, null);
        result5.setRecordId("5");
        result5.setId("5:Marker1");
        var result6 = new Result(6, "Marker2", 5.0, 7.0, null, null, null, "Sample1", "Tech1", null, null, null, null);
        result6.setRecordId("6");
        result6.setId("6:Marker2");
        resultService.save(result1);
        resultService.save(result2);
        resultService.save(result3);
        resultService.save(result4);
        resultService.save(result5);
        resultService.save(result6);

        var tech1 = new Technology("Tech1");
        tech1.setProperties(List.of(
                new TechnologyProperties("Marker1", null, null, List.of("Tech2", "Tech3"))));
        technologyService.save(tech1);
        technologyService.save(new Technology("Tech2"));
        technologyService.save(new Technology("Tech3"));

        markerService.save(new Marker("Marker1", null, null, "Unit1"));
        markerService.save(new Marker("Marker2", null, null, null));

        unitService.save(new Unit("Unit1", "U1"));
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingValue() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue(6.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(2);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRange() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setMaximum(7.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(2);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2);
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

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(5);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingValueWithAbsDeviation() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue(6.0);
        options.setAbsoluteDeviation(1.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(2);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingValueWithAbsDeviationMinOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue(6.0);
        options.setAbsoluteDeviation(1.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(3);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingValueWithAbsDeviationMaxOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue(6.0);
        options.setAbsoluteDeviation(1.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(4);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingValueWithAbsDeviationBothOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue(6.0);
        options.setAbsoluteDeviation(1.0);
        options.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(5);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingValueWithPercDeviation() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue(6.0);
        options.setPercentageDeviation(17.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(2);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingValueWithPercDeviationBothOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue(6.0);
        options.setPercentageDeviation(17.0);
        options.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(5);
    }

    @Test
    void processNumericSearch_ShouldThrow_WhenSearchingValueWithTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue(6.0);
        options.setAbsoluteTolerance(1.0);

        assertThrows(BadRequestException.class, () -> numericSearchService.processNumericSearch(options));
    }

    @Test
    void processNumericSearch_ShouldThrow_WhenSearchingExactMatchWithTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setMaximum(7.0);
        options.setAbsoluteTolerance(1.0);

        assertThrows(BadRequestException.class, () -> numericSearchService.processNumericSearch(options));
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingMinOFRWithAbsTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setMaximum(7.0);
        options.setAbsoluteTolerance(5.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(3);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingMaxOFRWithAbsTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setMaximum(7.0);
        options.setAbsoluteTolerance(2.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(4);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingBothOFRWithAbsTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setMaximum(7.0);
        options.setAbsoluteTolerance(4.0);
        options.setSearchType(NumericSearchType.BOTH_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(5);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingMinOFRWithPercTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setMaximum(7.0);
        options.setPercentageTolerance(50.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(3);
    }

    @Test
    void processNumericSearch_ShouldReturnEmpty_WhenSearchingMinOFRWithLowTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setMaximum(7.0);
        options.setAbsoluteTolerance(0.5);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).isEmpty();
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithTechnology() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(1.0);
        options.setMaximum(10.0);
        options.setFilters(new SearchFilters("Tech1", null, null, null, null));

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(3);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 4, 5);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithTechnologyComparable() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(1.0);
        options.setMaximum(10.0);
        options.setFilters(new SearchFilters("Tech1", true, null, null, null));

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(5);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithSample() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(1.0);
        options.setMaximum(10.0);
        options.setFilters(new SearchFilters(null, null, "Sample1", null, null));

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(2);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithSampleAndTechnology() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(1.0);
        options.setMaximum(10.0);
        options.setFilters(new SearchFilters("Tech1", null, "Sample1", null, null));


        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingValueInDifferentUnit() {
        var unit2 = new Unit("Unit2", "U2");
        unit2.setConversions(List.of(new Conversion("Unit1", "Marker1", "x*10")));
        unitService.save(unit2);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue(0.6);
        options.setUnitName("Unit2");

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(2);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingRangeInDifferentUnit() {
        var unit2 = new Unit("Unit2", "U2");
        unit2.setConversions(List.of(new Conversion("Unit1", "Marker1", "x*10")));
        unitService.save(unit2);

        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(0.5);
        options.setMaximum(0.7);
        options.setUnitName("Unit2");

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(2);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void processNumericSearch_ShouldThrow_WhenSearchingWithUnitAndMarkerWithoutUnit() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker2");
        options.setMinimum(0.5);
        options.setMaximum(0.7);
        options.setUnitName("Unit2");

        assertThrows(BadRequestException.class, () -> numericSearchService.processNumericSearch(options));
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
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMax() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(3);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2, 4);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMaxAndMinOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(2);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(3, 5);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMinAndMaxOFR() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMaximum(7.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(2);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(4, 5);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMaxAndMinOFRWithTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinimum(5.0);
        options.setAbsoluteTolerance(2.0);
        options.setSearchType(NumericSearchType.MIN_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(3);
    }

    @Test
    void processNumericSearch_ShouldReturnResults_WhenSearchingWithoutMinAndMaxOFRWithTolerance() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMarkerName("Marker1");
        options.setMaximum(7.0);
        options.setAbsoluteTolerance(1.0);
        options.setSearchType(NumericSearchType.MAX_OUT_OF_RANGE);

        var result = numericSearchService.processNumericSearch(options);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults()).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(4);
    }
}
