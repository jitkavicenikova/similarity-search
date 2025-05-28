package thesis.domain.processing.importer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.DataSet;
import thesis.data.validation.database.RecordDatabaseValidator;
import thesis.data.validation.database.ResultDatabaseValidator;
import thesis.data.validation.dataset.*;
import thesis.exceptions.ValidationException;

import java.util.List;
import java.util.Objects;

/**
 * Service class responsible for validating data sets.
 * It checks for duplicates and validates various entities within the data set.
 */
@Service
public class ValidationService {
    private final DuplicateValidationService duplicateValidationService;
    private final UnitDataSetValidator unitDataSetValidator;
    private final TechnologyDataSetValidator technologyDataSetValidator;
    private final MarkerDataSetValidator markerDataSetValidator;
    private final RecordDataSetValidator recordDataSetValidator;
    private final ResultDataSetValidator resultDataSetValidator;
    private final StringCategoryDataSetValidator stringCategoryDataSetValidator;
    private final RecordDatabaseValidator recordDatabaseValidator;
    private final ResultDatabaseValidator resultDatabaseValidator;

    @Autowired
    public ValidationService(DuplicateValidationService duplicateValidationService,
                             UnitDataSetValidator unitDataSetValidator,
                             TechnologyDataSetValidator technologyDataSetValidator,
                             MarkerDataSetValidator markerDataSetValidator,
                             RecordDataSetValidator recordDataSetValidator,
                             ResultDataSetValidator resultDataSetValidator,
                             StringCategoryDataSetValidator stringCategoryDataSetValidator,
                             RecordDatabaseValidator recordDatabaseValidator,
                             ResultDatabaseValidator resultDatabaseValidator) {
        this.duplicateValidationService = duplicateValidationService;
        this.unitDataSetValidator = unitDataSetValidator;
        this.technologyDataSetValidator = technologyDataSetValidator;
        this.markerDataSetValidator = markerDataSetValidator;
        this.recordDataSetValidator = recordDataSetValidator;
        this.resultDataSetValidator = resultDataSetValidator;
        this.stringCategoryDataSetValidator = stringCategoryDataSetValidator;
        this.recordDatabaseValidator = recordDatabaseValidator;
        this.resultDatabaseValidator = resultDatabaseValidator;
    }

    /**
     * Validates the given data set.
     * It checks for duplicates and validates various entities within the data set.
     *
     * @param dataSet the data set to be validated
     */
    public void validate(DataSet dataSet) {
        validateDataSetEntities(dataSet.getUnits(), unitDataSetValidator, dataSet);
        validateDataSetEntities(dataSet.getMarkers(), markerDataSetValidator, dataSet);
        validateDataSetEntities(dataSet.getTechnologies(), technologyDataSetValidator, dataSet);
        validateDataSetEntities(dataSet.getRecords(), recordDataSetValidator, dataSet);
        validateDataSetEntities(dataSet.getStringCategories(), stringCategoryDataSetValidator, dataSet);
        validateDataSetEntities(dataSet.getResults(), resultDataSetValidator, dataSet);

        duplicateValidationService.validate(dataSet);
    }

    /**
     * Validates the given data set for incremental updates.
     * It checks for duplicates and validates various entities within the data set.
     *
     * @param dataSet the data set to be validated
     */
    public void validateIncrement(DataSet dataSet) {
        checkForbiddenEntitiesInIncrement(dataSet);

        if (dataSet.getRecords() != null) {
            dataSet.getRecords().forEach(recordDatabaseValidator::runValidation);
        }

        if (dataSet.getResults() != null) {
            dataSet.getResults().forEach(result -> {
                resultDatabaseValidator.runValidation(result);
                checkRecordExists(dataSet, result.getRecordIdRaw());
            });
        }

        duplicateValidationService.validateIncrement(dataSet);
    }

    private <T> void validateDataSetEntities(List<T> entities, DataSetValidator<T> validator, DataSet dataSet) {
        if (entities != null) {
            entities.forEach(item -> validator.runValidation(dataSet, item));
        }
    }

    private void checkForbiddenEntitiesInIncrement(DataSet dataSet) {
        if (dataSet.getUnits() != null || dataSet.getMarkers() != null || dataSet.getTechnologies() != null || dataSet.getStringCategories() != null) {
            throw new ValidationException("Units, Markers, Technologies and StringCategories cannot be uploaded in increment");
        }
    }

    private void checkRecordExists(DataSet dataSet, Integer recordIdRaw) {
        if (dataSet.getRecords() == null || dataSet.getRecords().stream().noneMatch(record -> Objects.equals(record.getIdRaw(), recordIdRaw))) {
            throw new ValidationException("Record with id " + recordIdRaw + " does not exist");
        }
    }
}
