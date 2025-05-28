package thesis.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Result;
import thesis.data.repository.ResultRepository;
import thesis.data.repository.TechnologyRepository;
import thesis.exceptions.EntityNotFoundException;

import java.util.List;

@Service
public class ResultService extends BaseEntityService<Result> {
    private final ResultRepository resultRepository;
    private final TechnologyRepository technologyRepository;

    @Autowired
    public ResultService(ResultRepository resultRepository, TechnologyRepository technologyRepository) {
        super(resultRepository);
        this.resultRepository = resultRepository;
        this.technologyRepository = technologyRepository;
    }

    @Override
    public Result save(Result result) {
        if (result.getTechnologyName() != null) {
            saveResultWithTechnologyDeviations(result);
        }
        saveResultSearchIndex(result);

        return resultRepository.save(result);
    }

    @Override
    public Result getEntity(String id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Result with id '" + id + "' not found"));
    }

    public Result update(String id, Result result) {
        var dbResult = getEntity(id);
        deleteSearchIndexes(dbResult);

        dbResult.setMin(result.getMin());
        dbResult.setMax(result.getMax());
        dbResult.setStringValue(result.getStringValue());
        dbResult.setStringValueCategory(result.getStringValueCategory());
        dbResult.setBooleanValue(result.getBooleanValue());
        dbResult.setTechnologyName(result.getTechnologyName());
        dbResult.setSample(result.getSample());
        dbResult.setMinRaw(result.getMinRaw());
        dbResult.setMaxRaw(result.getMaxRaw());
        dbResult.setUnitRawName(result.getUnitRawName());
        dbResult.setTimestamp(result.getTimestamp());

        if (dbResult.getTechnologyName() != null) {
            saveResultWithTechnologyDeviations(dbResult);
        }
        saveResultSearchIndex(dbResult);

        return resultRepository.save(dbResult);
    }

    @Override
    public void delete(String id) {
        var result = getEntity(id);

        deleteSearchIndexes(result);

        resultRepository.deleteById(id);
    }

    public List<Result> getResultsByRecordId(String recordId) {
        return resultRepository.getAllByRecordId(recordId);
    }

    private void deleteSearchIndexes(Result result) {
        if (result.getMin() != null || result.getMax() != null) {
            resultRepository.deleteResultSearchIndex(result);
            resultRepository.deleteResultSearchIndexWithTechDeviations(result);
        }
    }

    private void saveResultWithTechnologyDeviations(Result result) {
        if (result.getStringValue() != null || result.getBooleanValue() != null) {
            return;
        }

        var newMin = result.getMin();
        var newMax = result.getMax();

        var isPercentage = technologyRepository.isDeviationPercentage(result.getTechnologyName(), result.getMarkerName());
        // if isPercentage is null, then deviation ranges don't exist for this technology and marker
        if (isPercentage != null) {
            if (result.getMin() != null) {
                var minDeviation = getDeviationForValue(result.getTechnologyName(), result.getMarkerName(), result.getMin());
                newMin = applyDeviation(result.getMin(), minDeviation, isPercentage, false);
            }

            if (result.getMax() != null) {
                var maxDeviation = getDeviationForValue(result.getTechnologyName(), result.getMarkerName(), result.getMax());
                newMax = applyDeviation(result.getMax(), maxDeviation, isPercentage, true);
            }
        }

        if (newMin == null) {
            newMin = Double.NEGATIVE_INFINITY;
        }
        if (newMax == null) {
            newMax = Double.POSITIVE_INFINITY;
        }

        resultRepository.saveResultSearchIndexWithTechDeviations(result, newMin, newMax);
    }

    private Double applyDeviation(Double value, Double deviation, boolean isPercentage, boolean isMax) {
        if (deviation == null) {
            return value;
        }

        return isPercentage
                ? value + (value * deviation / 100) * (isMax ? 1 : -1)
                : value + deviation * (isMax ? 1 : -1);
    }

    private Double getDeviationForValue(String technologyName, String markerName, Double value) {
        var fromDeviation = technologyRepository.getFromDeviation(technologyName, markerName, value)
                .stream()
                .findFirst()
                .orElse(null);

        var toDeviation = technologyRepository.getToDeviation(technologyName, markerName, value)
                .stream()
                .findFirst()
                .orElse(null);

        if (fromDeviation == null || !fromDeviation.equals(toDeviation)) {
            return null;
        }

        return Double.parseDouble(fromDeviation.split("::")[2]);
    }

    private void saveResultSearchIndex(Result result) {
        if (result.getStringValue() != null || result.getBooleanValue() != null) {
            return;
        }

        if (result.getMin() == null) {
            result.setMin(Double.NEGATIVE_INFINITY);
        }
        if (result.getMax() == null) {
            result.setMax(Double.POSITIVE_INFINITY);
        }
        resultRepository.saveResultSearchIndex(result);
    }
}
