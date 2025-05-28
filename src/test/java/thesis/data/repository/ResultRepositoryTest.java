package thesis.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import thesis.RedisContainerTestBase;
import thesis.data.model.Result;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("soil")
public class ResultRepositoryTest extends RedisContainerTestBase {
    private final Result result = new Result(0, "Marker1", 1.5, 2.5, "stringResult", "hierarchy", true,
            "sample", "technology", 5D, 6D, "Unit2", LocalDateTime.now());
    @Autowired
    private ResultRepository repository;

    @DynamicPropertySource
    static void setRedisProperties(DynamicPropertyRegistry registry) {
        redisContainer.start();
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Test
    public void testSaveResultSearchIndexWithTechDeviations() {
        result.setId("1:Marker1");
        result.setRecordId("1");
        repository.saveResultSearchIndexWithTechDeviations(result, 1.1, 2.2);

        var minValues = redisTemplate.opsForZSet().rangeByScore("marker:Marker1:technology:result:min", 1.1, 1.1);
        var maxValues = redisTemplate.opsForZSet().rangeByScore("marker:Marker1:technology:result:max", 2.2, 2.2);

        assertNotNull(minValues);
        assertTrue(minValues.contains("1"));
        assertNotNull(maxValues);
        assertTrue(maxValues.contains("1"));
    }

    @Test
    public void testSearchForMinimumMatches() {
        result.setId("2:Marker1");
        result.setRecordId("2");
        repository.saveResultSearchIndexWithTechDeviations(result, 1.5, 2.5);

        Set<String> matches = repository.searchForMinimumMatches("Marker1", 1.4, 1.6, true);
        assertNotNull(matches);
        assertTrue(matches.contains("2"));
    }

    @Test
    public void testGetTechnologyName() {
        result.setId("3:Marker1");
        repository.save(result);

        String technologyName = repository.getTechnologyName("3", "Marker1");
        assertEquals("technology", technologyName);
    }

    @Test
    public void testGetAllMinResultsForMarker() {
        result.setId("4:Marker1");
        result.setRecordId("4");
        repository.saveResultSearchIndexWithTechDeviations(result, 1.7, 2.7);

        Set<ZSetOperations.TypedTuple<String>> minResults = repository.getAllMinResultsForMarker("Marker1", true);
        assertNotNull(minResults);
        assertTrue(minResults.stream().anyMatch(tuple -> tuple.getValue().equals("4") && tuple.getScore().equals(1.7)));
    }

    @Test
    public void testDeleteResultSearchIndexWithTechDeviations() {
        result.setId("5:Marker1");
        result.setRecordId("5");
        repository.saveResultSearchIndexWithTechDeviations(result, 1.9, 2.9);

        repository.deleteResultSearchIndexWithTechDeviations(result);

        var minValues = redisTemplate.opsForZSet().rangeByScore("marker:Marker1:technology:result:min", 1.9, 1.9);
        assertTrue(minValues == null || minValues.isEmpty());
    }
}
