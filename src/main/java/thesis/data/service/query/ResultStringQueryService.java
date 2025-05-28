package thesis.data.service.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Result;
import thesis.data.model.StringCategory;
import thesis.data.repository.ResultRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for querying results based on string values.
 */
@Service
public class ResultStringQueryService {
    private final ResultRepository resultRepository;

    @Autowired
    public ResultStringQueryService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public List<Result> getAllStringResultsForSingleValue(String markerName, String value, StringCategory category) {
        return getResultsForValue(markerName, value, category);
    }

    /**
     * Retrieves all results for a given marker name and a list of string values, filtered by category.
     *
     * @param markerName The name of the marker.
     * @param values     A list of string values to search for.
     * @param category   The category to filter the results by. Can be null.
     * @return A list of results matching the marker name, values, and category.
     */
    public List<Result> getAllStringResultsForMultipleValues(String markerName, List<String> values, StringCategory category) {
        List<Result> results = new ArrayList<>();

        for (String value : values) {
            results.addAll(getResultsForValue(markerName, value, category));
        }

        return results;
    }

    /**
     * Retrieves all results for a given marker name and string value category.
     *
     * @param markerName The name of the marker.
     * @param category   The string value category to filter the results by.
     * @return A list of results matching the marker name and string value category.
     */
    public List<Result> getAllStringResultsForCategory(String markerName, StringCategory category) {
        return resultRepository.getAllByMarkerNameAndStringValueCategory(markerName, category.getName());
    }

    private List<Result> getResultsForValue(String markerName, String value, StringCategory category) {
        return category == null
                ? resultRepository.getAllByMarkerNameAndStringValue(markerName, value)
                : resultRepository.getAllByMarkerNameAndStringValueAndStringValueCategory(markerName, value, category.getName());
    }
}
