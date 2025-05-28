package thesis.data.repository.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import thesis.data.model.DeviationRange;
import thesis.data.model.TechnologyProperties;
import thesis.utils.EntityUtils;
import thesis.utils.RedisKeyBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
public class CustomTechnologyRepositoryImpl implements CustomTechnologyRepository {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public CustomTechnologyRepositoryImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveIsPercentage(TechnologyProperties technologyProperties, String technologyName) {
        var deviationKey = RedisKeyBuilder.technologyMarkerKey(technologyName, technologyProperties.getMarkerName());
        redisTemplate.opsForHash().put(deviationKey, "isPercentage", String.valueOf(technologyProperties.getIsPercentage()));
    }

    @Override
    public void saveSensitivity(String technologyName, String markerName, Double sensitivity) {
        redisTemplate.opsForZSet().add(RedisKeyBuilder.sensitivityKey(markerName), technologyName, sensitivity);
    }

    @Override
    public void saveSpecificity(String technologyName, String markerName, Double specificity) {
        redisTemplate.opsForZSet().add(RedisKeyBuilder.specificityKey(markerName), technologyName, specificity);
    }

    @Override
    public void saveDeviationRanges(List<DeviationRange> ranges, String technologyName, String markerName) {
        var fromKey = RedisKeyBuilder.deviationRangeFromKey(technologyName, markerName);
        var toKey = RedisKeyBuilder.deviationRangeToKey(technologyName, markerName);
        ranges.sort(Comparator.comparingDouble(DeviationRange::getFrom));

        Double previousToValue = null;

        for (var dev : ranges) {
            var value = EntityUtils.generateDeviationRangeId(dev);

            var newFrom = dev.getFrom();
            if (previousToValue != null && Objects.equals(previousToValue, dev.getFrom())) {
                // Adjust 'from' value using Math.ulp() to avoid overlap
                newFrom = dev.getFrom() + Math.ulp(dev.getFrom());
            }

            redisTemplate.opsForZSet().add(fromKey, value, newFrom);
            redisTemplate.opsForZSet().add(toKey, value, dev.getTo());

            previousToValue = dev.getTo();
        }
    }

    @Override
    public Set<String> getFromDeviation(String technologyName, String markerName, Double value) {
        var rangeKey = RedisKeyBuilder.deviationRangeFromKey(technologyName, markerName);
        return redisTemplate.opsForZSet().reverseRangeByScore(rangeKey, Double.NEGATIVE_INFINITY, value, 0, 1);
    }

    @Override
    public Set<String> getToDeviation(String technologyName, String markerName, Double value) {
        var rangeKey = RedisKeyBuilder.deviationRangeToKey(technologyName, markerName);
        return redisTemplate.opsForZSet().rangeByScore(rangeKey, value, Double.POSITIVE_INFINITY, 0, 1);
    }

    @Override
    public Boolean isDeviationPercentage(String technologyName, String markerName) {
        var key = RedisKeyBuilder.technologyMarkerKey(technologyName, markerName);
        var result = redisTemplate.opsForHash().get(key, "isPercentage");
        return result == null ? null : Boolean.parseBoolean(result.toString());
    }

    @Override
    public Set<String> searchForTechnologyWithMinSensitivity(String markerName, Double sensitivity) {
        var key = RedisKeyBuilder.sensitivityKey(markerName);
        return redisTemplate.opsForZSet().rangeByScore(key, sensitivity, 100);
    }

    @Override
    public Set<String> searchForTechnologyWithMinSpecificity(String markerName, Double specificity) {
        var key = RedisKeyBuilder.specificityKey(markerName);
        return redisTemplate.opsForZSet().rangeByScore(key, specificity, 100);
    }

    @Override
    public void deleteDeviation(String markerName, String technologyName) {
        redisTemplate.delete(RedisKeyBuilder.technologyMarkerKey(technologyName, markerName));
    }

    @Override
    public void deleteSensitivity(String markerName, String technologyName) {
        redisTemplate.opsForZSet().remove(RedisKeyBuilder.sensitivityKey(markerName), technologyName);
    }

    @Override
    public void deleteSpecificity(String markerName, String technologyName) {
        redisTemplate.opsForZSet().remove(RedisKeyBuilder.specificityKey(markerName), technologyName);
    }

    @Override
    public void deleteDeviationRange(List<DeviationRange> ranges, String markerName, String technologyName) {
        var deviationIds = ranges.stream()
                .map(EntityUtils::generateDeviationRangeId)
                .toArray();
        redisTemplate.opsForZSet()
                .remove(RedisKeyBuilder.deviationRangeFromKey(technologyName, markerName), deviationIds);
        redisTemplate.opsForZSet()
                .remove(RedisKeyBuilder.deviationRangeToKey(technologyName, markerName), deviationIds);
    }
}
