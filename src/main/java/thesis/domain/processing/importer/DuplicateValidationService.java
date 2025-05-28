package thesis.domain.processing.importer;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import thesis.data.model.Record;
import thesis.data.model.*;
import thesis.exceptions.ValidationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Service class responsible for validating duplicates in data sets.
 */
@Service
public class DuplicateValidationService {
    /**
     * Validates the given data set for duplicates.
     *
     * @param dataSet the data set to be validated
     * @throws ValidationException if duplicates are found
     */
    public void validate(DataSet dataSet) {
        validateDuplicates(dataSet.getMarkers(), Marker::getName, "Duplicate marker with name ");
        validateDuplicates(dataSet.getRecords(), Record::getIdRaw, "Duplicate record with id ");
        validateDuplicates(dataSet.getTechnologies(), Technology::getName, "Duplicate technology with name ");
        validateDuplicates(dataSet.getUnits(), Unit::getName, "Duplicate unit with name ");
        validateDuplicates(dataSet.getStringCategories(), StringCategory::getName, "Duplicate stringCategory with name ");
        validateDuplicates(dataSet.getResults(), result -> Pair.of(result.getRecordIdRaw(), result.getMarkerName()),
                "Duplicate result with record id %s and marker name %s");
    }

    /**
     * Validates the given data set for duplicates during an increment operation.
     *
     * @param dataSet the data set to be validated
     * @throws ValidationException if duplicates are found
     */
    public void validateIncrement(DataSet dataSet) {
        validateDuplicates(dataSet.getRecords(), Record::getIdRaw, "Duplicate record with id ");
        validateDuplicates(dataSet.getResults(), result -> Pair.of(result.getRecordIdRaw(), result.getMarkerName()),
                "Duplicate result with record id %s and marker name %s");
    }

    private <T, K> void validateDuplicates(List<T> entities, Function<T, K> keyExtractor, String errorMessage) {
        if (entities != null) {
            Set<K> seenKeys = new HashSet<>();
            for (var item : entities) {
                K key = keyExtractor.apply(item);
                if (!seenKeys.add(key)) {
                    if (key instanceof Pair<?, ?> pair) {
                        throw new ValidationException(String.format(errorMessage, pair.getLeft(), pair.getRight()));
                    }
                    throw new ValidationException(errorMessage + key);
                }
            }
        }
    }
}
