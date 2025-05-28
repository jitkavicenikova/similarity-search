package thesis.integration.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import thesis.RedisContainerTestBase;
import thesis.data.model.Marker;
import thesis.data.model.Result;
import thesis.data.model.StringCategory;
import thesis.data.repository.MarkerRepository;
import thesis.data.repository.ResultRepository;
import thesis.data.repository.StringCategoryRepository;
import thesis.domain.search.dto.StringSearchOptions;
import thesis.domain.search.dto.enums.StringSearchType;
import thesis.domain.search.service.StringSearchService;
import thesis.exceptions.BadRequestException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("soil")
public class StringSearchIntegrationTest extends RedisContainerTestBase {
    @Autowired
    private StringSearchService stringSearchService;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private MarkerRepository markerRepository;

    @Autowired
    private StringCategoryRepository stringCategoryRepository;

    @DynamicPropertySource
    static void setRedisProperties(DynamicPropertyRegistry registry) {
        redisContainer.start();
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    void setUpData() {
        // Populate Redis with test data
        resultRepository.save(new Result(1, "Marker1", null, null, "value1", "cat1", null, null, null, null, null, null, null));
        resultRepository.save(new Result(2, "Marker1", null, null, "value1", null, null, null, null, null, null, null, null));
        resultRepository.save(new Result(3, "Marker1", null, null, "value2", "cat1", null, null, null, null, null, null, null));
        resultRepository.save(new Result(4, "Marker1", null, null, "value2", null, null, null, null, null, null, null, null));
        resultRepository.save(new Result(5, "Marker1", null, null, "value3", "cat1", null, null, null, null, null, null, null));
        resultRepository.save(new Result(6, "Marker1", null, null, "value3", null, null, null, null, null, null, null, null));
        resultRepository.save(new Result(7, "Marker2", null, null, "value1", null, null, null, null, null, null, null, null));

        stringCategoryRepository.save(new StringCategory("cat1", true, List.of("value1", "value2", "value3")));
        stringCategoryRepository.save(new StringCategory("cat2", false, List.of("value1", "value2", "value3")));

        markerRepository.save(new Marker("Marker1", null, null, null));
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenSearchingValue() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue("value1");

        List<Result> results = stringSearchService.processStringSearch(options);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenSearchingValues() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("Marker1");
        options.setValues(List.of("value1", "value2"));

        List<Result> results = stringSearchService.processStringSearch(options);

        assertThat(results).hasSize(4);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 2, 3, 4);
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenSearchingCategory() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("Marker1");
        options.setCategoryName("cat1");

        List<Result> results = stringSearchService.processStringSearch(options);

        assertThat(results).hasSize(3);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 3, 5);
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenSearchingExactMatchInComparableCategory() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue("value2");
        options.setCategoryName("cat1");

        List<Result> results = stringSearchService.processStringSearch(options);

        assertThat(results).hasSize(1);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(3);
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenSearchingLessThanInComparableCategory() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue("value2");
        options.setCategoryName("cat1");
        options.setSearchType(StringSearchType.LESS_THAN);

        List<Result> results = stringSearchService.processStringSearch(options);

        assertThat(results).hasSize(1);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1);
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenSearchingLessThanOrEqualInComparableCategory() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue("value2");
        options.setCategoryName("cat1");
        options.setSearchType(StringSearchType.LESS_THAN_OR_EQUAL);

        List<Result> results = stringSearchService.processStringSearch(options);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 3);
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenSearchingGreaterThanInComparableCategory() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue("value2");
        options.setCategoryName("cat1");
        options.setSearchType(StringSearchType.GREATER_THAN);

        List<Result> results = stringSearchService.processStringSearch(options);

        assertThat(results).hasSize(1);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(5);
    }

    @Test
    void processStringSearch_ShouldReturnResults_WhenSearchingGreaterThanOrEqualInComparableCategory() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue("value2");
        options.setCategoryName("cat1");
        options.setSearchType(StringSearchType.GREATER_THAN_OR_EQUAL);

        List<Result> results = stringSearchService.processStringSearch(options);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(3, 5);
    }

    @Test
    void processStringSearch_ShouldThrow_WhenSearchingLessThanInNonComparableCategory() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue("value2");
        options.setCategoryName("cat2");
        options.setSearchType(StringSearchType.GREATER_THAN_OR_EQUAL);

        assertThrows(BadRequestException.class, () -> stringSearchService.processStringSearch(options));
    }

    @Test
    void processStringSearch_ShouldThrow_WhenInvalidOptions() {
        StringSearchOptions options = new StringSearchOptions();
        options.setMarkerName("Marker1");

        assertThrows(BadRequestException.class, () -> stringSearchService.processStringSearch(options));
    }
}
