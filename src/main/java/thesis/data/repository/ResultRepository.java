package thesis.data.repository;

import org.springframework.data.repository.CrudRepository;
import thesis.data.model.Result;
import thesis.data.repository.custom.CustomResultRepository;

import java.util.List;

public interface ResultRepository extends CrudRepository<Result, String>, CustomResultRepository {
    List<Result> getAllByMarkerNameAndStringValue(String markerName, String stringValue);

    List<Result> getAllByMarkerNameAndStringValueAndStringValueCategory(String markerName, String stringValue, String stringValueCategory);

    List<Result> getAllByMarkerNameAndBooleanValue(String markerName, Boolean booleanValue);

    List<Result> getAllByMarkerNameAndBooleanValueAndTechnologyName(String markerName, Boolean booleanValue, String technologyName);

    List<Result> getAllByMarkerNameAndStringValueCategory(String markerName, String stringValueCategory);

    List<Result> getAllByRecordId(String recordId);

    Boolean existsByMarkerName(String markerName);

    Boolean existsByRecordId(String recordId);

    Boolean existsByStringValueCategory(String stringValueCategory);

    Boolean existsByTechnologyName(String technologyName);
}
