package thesis.data.repository.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;
import thesis.data.model.Result;
import thesis.utils.EntityUtils;
import thesis.utils.RedisKeyBuilder;

import java.util.Set;

@Repository
public class CustomResultRepositoryImpl implements CustomResultRepository {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public CustomResultRepositoryImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveResultSearchIndex(Result result) {
        redisTemplate.opsForZSet().add(RedisKeyBuilder.resultMinKey(result.getMarkerName()), result.getRecordId(), result.getMin());
        redisTemplate.opsForZSet().add(RedisKeyBuilder.resultMaxKey(result.getMarkerName()), result.getRecordId(), result.getMax());
    }

    @Override
    public void saveResultSearchIndexWithTechDeviations(Result result, Double min, Double max) {
        redisTemplate.opsForZSet().add(RedisKeyBuilder.techResultMinKey(result.getMarkerName()), result.getRecordId(), min);
        redisTemplate.opsForZSet().add(RedisKeyBuilder.techResultMaxKey(result.getMarkerName()), result.getRecordId(), max);
    }

    @Override
    public Set<String> searchForMinimumMatches(String markerName, Double min, Double max, Boolean withTechDeviations) {
        var indexKey = withTechDeviations
                ? RedisKeyBuilder.techResultMinKey(markerName)
                : RedisKeyBuilder.resultMinKey(markerName);
        return redisTemplate.opsForZSet().rangeByScore(indexKey, min, max);
    }

    @Override
    public Set<String> searchForMaximumMatches(String markerName, Double min, Double max, Boolean withTechDeviations) {
        var indexKey = withTechDeviations
                ? RedisKeyBuilder.techResultMaxKey(markerName)
                : RedisKeyBuilder.resultMaxKey(markerName);
        return redisTemplate.opsForZSet().rangeByScore(indexKey, min, max);
    }

    @Override
    public String getTechnologyName(String recordId, String markerName) {
        return (String) redisTemplate.opsForHash()
                .get(RedisKeyBuilder.resultHashKey(recordId, markerName), "technologyName");
    }

    @Override
    public Set<TypedTuple<String>> getAllMinResultsForMarker(String markerName, Boolean withTechDeviations) {
        var indexKey = withTechDeviations
                ? RedisKeyBuilder.techResultMinKey(markerName)
                : RedisKeyBuilder.resultMinKey(markerName);
        return redisTemplate.opsForZSet().rangeWithScores(indexKey, 0, -1);
    }

    @Override
    public Set<TypedTuple<String>> getAllMaxResultsForMarker(String markerName, Boolean withTechDeviations) {
        var indexKey = withTechDeviations
                ? RedisKeyBuilder.techResultMaxKey(markerName)
                : RedisKeyBuilder.resultMaxKey(markerName);
        return redisTemplate.opsForZSet().rangeWithScores(indexKey, 0, -1);
    }

    @Override
    public void deleteResultSearchIndex(Result result) {
        redisTemplate.opsForZSet().remove(RedisKeyBuilder.resultMinKey(result.getMarkerName()), result.getRecordId());
        redisTemplate.opsForZSet().remove(RedisKeyBuilder.resultMaxKey(result.getMarkerName()), result.getRecordId());
    }

    @Override
    public void deleteResultSearchIndexWithTechDeviations(Result result) {
        redisTemplate.opsForZSet().remove(RedisKeyBuilder.techResultMinKey(result.getMarkerName()), result.getRecordId());
        redisTemplate.opsForZSet().remove(RedisKeyBuilder.techResultMaxKey(result.getMarkerName()), result.getRecordId());
    }
}
