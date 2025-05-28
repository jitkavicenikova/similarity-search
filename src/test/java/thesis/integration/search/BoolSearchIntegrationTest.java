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
import thesis.data.model.Technology;
import thesis.data.repository.MarkerRepository;
import thesis.data.repository.ResultRepository;
import thesis.data.repository.TechnologyRepository;
import thesis.domain.search.dto.BoolSearchOptions;
import thesis.domain.search.dto.SearchFilters;
import thesis.domain.search.service.BoolSearchService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("soil")
public class BoolSearchIntegrationTest extends RedisContainerTestBase {
    @Autowired
    private BoolSearchService boolSearchService;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private MarkerRepository markerRepository;

    @Autowired
    private TechnologyRepository technologyRepository;

    @DynamicPropertySource
    static void setRedisProperties(DynamicPropertyRegistry registry) {
        redisContainer.start();
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    void setUpData() {
        // Populate Redis with test data
        resultRepository.save(new Result(1, "Marker1", null, null, null, null, true, null, "Tech1", null, null, null, null));
        resultRepository.save(new Result(2, "Marker1", null, null, null, null, false, null, "Tech2", null, null, null, null));
        resultRepository.save(new Result(3, "Marker1", null, null, null, null, true, null, "Tech3", null, null, null, null));
        resultRepository.save(new Result(4, "Marker2", null, null, null, null, false, null, "Tech1", null, null, null, null));

        technologyRepository.save(new Technology("Tech1"));
        technologyRepository.save(new Technology("Tech2"));
        technologyRepository.save(new Technology("Tech3"));

        redisTemplate.opsForZSet().add("marker:Marker1:sensitivity", "Tech1", 0.9);
        redisTemplate.opsForZSet().add("marker:Marker1:specificity", "Tech1", 0.9);
        redisTemplate.opsForZSet().add("marker:Marker1:sensitivity", "Tech3", 0.7);
        redisTemplate.opsForZSet().add("marker:Marker1:specificity", "Tech3", 0.7);

        markerRepository.save(new Marker("Marker1", null, null, null));
        markerRepository.save(new Marker("Marker2", null, null, null));
    }

    @Test
    void processBoolSearch_ShouldReturnResults_WhenSearchingValue() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("Marker1");
        options.setValue(true);

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1, 3);
    }

    @Test
    void processBoolSearch_ShouldReturnResults_WhenSearchingValueAndTechnology() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("Marker1");
        options.setFilters(new SearchFilters("Tech3", null, null, null, null));
        options.setValue(true);

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertThat(results).hasSize(1);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(3);
    }

    @Test
    void processBoolSearch_ShouldReturnResults_WhenSearchingValueAndSpecificity() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinSpecificity(0.8);
        options.setValue(true);

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertThat(results).hasSize(1);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1);
    }

    @Test
    void processBoolSearch_ShouldReturnResults_WhenSearchingValueAndSensitivity() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinSensitivity(0.8);
        options.setValue(true);

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertThat(results).hasSize(1);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1);
    }

    @Test
    void processBoolSearch_ShouldReturnResults_WhenSearchingValueAndSpecificityAndSensitivity() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinSensitivity(0.8);
        options.setMinSpecificity(0.8);
        options.setValue(true);

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertThat(results).hasSize(1);
        assertThat(results).extracting(Result::getRecordIdRaw).containsExactlyInAnyOrder(1);
    }

    @Test
    void processBoolSearch_ShouldReturnEmpty_WhenSearchingSpecificityAndSensitivityTooHigh() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinSensitivity(0.95);
        options.setMinSpecificity(0.95);
        options.setValue(true);

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertThat(results).isEmpty();
    }

    @Test
    void processBoolSearch_ShouldReturnEmpty_WhenSearchingSpecificityTooHigh() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinSpecificity(0.95);
        options.setValue(true);

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertThat(results).isEmpty();
    }

    @Test
    void processBoolSearch_ShouldReturnEmpty_WhenSearchingSensitivityTooHigh() {
        BoolSearchOptions options = new BoolSearchOptions();
        options.setMarkerName("Marker1");
        options.setMinSensitivity(0.95);
        options.setValue(true);

        List<Result> results = boolSearchService.processBoolSearch(options);

        assertThat(results).isEmpty();
    }
}

