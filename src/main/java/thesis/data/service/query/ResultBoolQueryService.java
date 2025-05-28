package thesis.data.service.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Result;
import thesis.data.repository.ResultRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Service class for querying results based on boolean values.
 */
@Service
public class ResultBoolQueryService {
    private final ResultRepository resultRepository;

    @Autowired
    public ResultBoolQueryService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    /**
     * Retrieves all results for a given marker name and boolean value.
     *
     * @param markerName       The name of the marker.
     * @param value            The boolean value to search for.
     * @param technologyNames  A set of technology names to filter the results.
     * @return A list of results matching the criteria.
     */
    public List<Result> getAllBoolResults(String markerName, Boolean value, Set<String> technologyNames) {
        if (technologyNames.isEmpty()) {
            return resultRepository.getAllByMarkerNameAndBooleanValue(markerName, value);
        }

        List<Result> results = new ArrayList<>();
        for (String technologyName : technologyNames) {
            results.addAll(resultRepository.getAllByMarkerNameAndBooleanValueAndTechnologyName(markerName, value, technologyName));
        }

        return results;
    }
}
