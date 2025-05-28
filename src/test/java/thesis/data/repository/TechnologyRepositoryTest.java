package thesis.data.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import thesis.RedisContainerTestBase;
import thesis.data.model.DeviationRange;
import thesis.data.model.TechnologyProperties;
import thesis.utils.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("soil")
public class TechnologyRepositoryTest extends RedisContainerTestBase {
    @Autowired
    private TechnologyRepository repository;

    private ZSetOperations<String, String> zSetOperations;

    @DynamicPropertySource
    static void setRedisProperties(DynamicPropertyRegistry registry) {
        redisContainer.start();
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    public void setUp() {
        zSetOperations = redisTemplate.opsForZSet();
    }

    @Test
    public void testSaveIsPercentage() {
        TechnologyProperties technologyProperties = new TechnologyProperties();
        technologyProperties.setMarkerName("Marker1");
        technologyProperties.setIsPercentage(true);

        repository.saveIsPercentage(technologyProperties, "Tech1");
        String isPercentage = (String) redisTemplate.opsForHash().get("technology:Tech1:marker:Marker1", "isPercentage");
        assertEquals("true", isPercentage);
    }

    @Test
    public void testSaveSensitivity() {
        repository.saveSensitivity("Tech1", "Marker1", 0.85);
        Set<String> result = zSetOperations.rangeByScore("marker:Marker1:sensitivity", 0.85, 0.85);
        assertNotNull(result);
        assertTrue(result.contains("Tech1"));
    }

    @Test
    public void testSaveSpecificity() {
        repository.saveSpecificity("Tech1", "Marker1", 0.95);
        Set<String> result = zSetOperations.rangeByScore("marker:Marker1:specificity", 0.95, 0.95);
        assertNotNull(result);
        assertTrue(result.contains("Tech1"));
    }

    @Test
    public void testSaveDeviationRanges() {
        DeviationRange range1 = new DeviationRange(1.0, 2.0, 0.5);
        DeviationRange range2 = new DeviationRange(2.0, 3.0, 0.8);

        List<DeviationRange> ranges = new ArrayList<>(List.of(range1, range2));

        repository.saveDeviationRanges(ranges, "Tech1", "Marker1");

        assertEquals(Double.valueOf(1.0), zSetOperations.score("technology:Tech1:marker:Marker1:range:from", "1.0::2.0::0.5"));
        assertEquals(Double.valueOf(3.0), zSetOperations.score("technology:Tech1:marker:Marker1:range:to", "2.0::3.0::0.8"));
    }

    @Test
    public void testGetFromDeviation() {
        zSetOperations.add("technology:Tech1:marker:Marker1:range:from", "0.8", 1.5);

        Set<String> result = repository.getFromDeviation("Tech1", "Marker1", 1.5);

        assertNotNull(result);
        assertTrue(result.contains("0.8"));
    }

    @Test
    public void testGetToDeviation() {
        zSetOperations.add("technology:Tech1:marker:Marker1:range:to", "0.5", 1.5);

        Set<String> result = repository.getToDeviation("Tech1", "Marker1", 1.5);

        assertNotNull(result);
        assertTrue(result.contains("0.5"));
    }

    @Test
    public void testIsDeviationPercentage() {
        redisTemplate.opsForHash().put("technology:Tech1:marker:Marker1", "isPercentage", "true");

        Boolean isPercentage = repository.isDeviationPercentage("Tech1", "Marker1");
        assertTrue(isPercentage);
    }

    @Test
    public void testSearchForTechnologyWithSensitivityGreaterThan() {
        zSetOperations.add("marker:Marker1:sensitivity", "Tech1", 0.95);

        Set<String> result = repository.searchForTechnologyWithMinSensitivity("Marker1", 0.9);

        assertNotNull(result);
        assertTrue(result.contains("Tech1"));
    }

    @Test
    public void testSearchForTechnologyWithSpecificityGreaterThan() {
        zSetOperations.add("marker:Marker1:specificity", "Tech2", 0.85);

        Set<String> result = repository.searchForTechnologyWithMinSpecificity("Marker1", 0.8);

        assertNotNull(result);
        assertTrue(result.contains("Tech2"));
    }

    @Test
    public void testDeleteDeviation() {
        redisTemplate.opsForHash().put("technology:Tech1:marker:Marker1", "isPercentage", "true");

        repository.deleteDeviation("Marker1", "Tech1");

        assertNull(redisTemplate.opsForHash().get("technology:Tech1:marker:Marker1", "isPercentage"));
    }

    @Test
    public void testDeleteSensitivity() {
        zSetOperations.add("marker:Marker1:sensitivity", "Tech1", 0.85);

        repository.deleteSensitivity("Marker1", "Tech1");

        Set<String> result = zSetOperations.rangeByScore("marker:Marker1:sensitivity", 0.85, 0.85);
        assertFalse(result.contains("Tech1"));
    }

    @Test
    public void testDeleteSpecificity() {
        zSetOperations.add("marker:Marker1:specificity", "Tech1", 0.95);

        repository.deleteSpecificity("Marker1", "Tech1");

        Set<String> result = zSetOperations.rangeByScore("marker:Marker1:specificity", 0.95, 0.95);
        assertFalse(result.contains("Tech1"));
    }

    @Test
    public void testDeleteDeviationRange() {
        DeviationRange range1 = new DeviationRange(1.0, 2.0, 0.5);
        var rangeId = EntityUtils.generateDeviationRangeId(range1);

        zSetOperations.add("technology:Tech1:marker:Marker1:range:to", rangeId, 2.0);
        zSetOperations.add("technology:Tech1:marker:Marker1:range:from", rangeId, 1.0);
        repository.deleteDeviationRange(List.of(range1), "Marker1", "Tech1");

        assertNull(zSetOperations.score("technology:Tech1:marker:Marker1:range:from", rangeId));
        assertNull(zSetOperations.score("technology:Tech1:marker:Marker1:range:to", rangeId));
    }
}