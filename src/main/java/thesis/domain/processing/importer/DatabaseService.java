package thesis.domain.processing.importer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.DataSet;
import thesis.data.model.Record;
import thesis.data.service.*;
import thesis.utils.EntityUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Service class responsible for saving data sets to the database.
 */
@Service
public class DatabaseService {
    private final MarkerService markerService;
    private final UnitService unitService;
    private final RecordService recordService;
    private final ResultService resultService;
    private final TechnologyService technologyService;
    private final StringCategoryService stringCategoryService;

    @Autowired
    public DatabaseService(MarkerService markerService, UnitService unitService,
                           RecordService recordService, ResultService resultService,
                           TechnologyService technologyService, StringCategoryService stringCategoryService) {
        this.markerService = markerService;
        this.unitService = unitService;
        this.recordService = recordService;
        this.resultService = resultService;
        this.technologyService = technologyService;
        this.stringCategoryService = stringCategoryService;
    }

    /**
     * Saves the given data set to the database.
     *
     * @param dataSet the data set to be saved
     */
    public void saveDataSet(DataSet dataSet) {
        saveEntities(dataSet.getUnits(), unitService::save);
        saveEntities(dataSet.getMarkers(), markerService::save);
        saveEntities(dataSet.getTechnologies(), technologyService::save);
        saveEntities(dataSet.getStringCategories(), stringCategoryService::save);

        var recordIdMap = saveRecordsAndReturnIdMap(dataSet);
        updateResultIds(dataSet, recordIdMap);
        saveEntities(dataSet.getResults(), resultService::save);
    }

    private <T> void saveEntities(List<T> entities, Consumer<T> saveFunction) {
        if (isNotEmpty(entities)) {
            entities.forEach(saveFunction);
        }
    }

    private Map<Integer, String> saveRecordsAndReturnIdMap(DataSet dataSet) {
        Map<Integer, String> recordIdMap = new HashMap<>();
        if (isNotEmpty(dataSet.getRecords())) {
            for (Record record : dataSet.getRecords()) {
                record.setMetadata(EntityUtils.generateRecordMetadata(dataSet, record));
                int oldId = record.getIdRaw();
                String newId = recordService.save(record).getId();
                recordIdMap.put(oldId, newId);
            }
        }

        return recordIdMap;
    }

    private void updateResultIds(DataSet dataSet, Map<Integer, String> recordIdMap) {
        if (isNotEmpty(dataSet.getResults())) {
            dataSet.getResults().forEach(result -> {
                String newRecordId = recordIdMap.get(result.getRecordIdRaw());
                if (newRecordId == null) {
                    throw new IllegalStateException("Record not found for ID: " + result.getRecordIdRaw());
                }
                result.setRecordId(newRecordId);
                result.setId(EntityUtils.generateResultId(newRecordId, result.getMarkerName()));
            });
        }
    }

    private <T> boolean isNotEmpty(List<T> list) {
        return list != null && !list.isEmpty();
    }
}
