package thesis.domain.manipulation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.mapper.RecordMapper;
import thesis.data.model.Record;
import thesis.data.service.RecordService;
import thesis.data.service.ResultService;
import thesis.data.validation.database.RecordDatabaseValidator;
import thesis.domain.manipulation.dto.RecordUpdateDto;
import thesis.exceptions.EntityNotFoundException;

@Service
public class RecordManipulationService extends BaseEntityManipulationService<Record> {
    private final ResultService resultService;
    private final RecordService recordService;
    private final RecordDatabaseValidator recordDatabaseValidator;
    private final RecordMapper recordMapper;

    @Autowired
    public RecordManipulationService(RecordService recordService, RecordDatabaseValidator recordDatabaseValidator,
                                     ResultService resultService, RecordMapper recordMapper) {
        super(recordService, recordDatabaseValidator);
        this.resultService = resultService;
        this.recordService = recordService;
        this.recordDatabaseValidator = recordDatabaseValidator;
        this.recordMapper = recordMapper;
    }

    /**
     * Deletes a record and all its results.
     *
     * @param id the ID of the record to delete
     * @throws EntityNotFoundException if the record with the given ID does not exist
     */
    public void deleteWithResults(String id) {
        if (!recordService.existsById(id)) {
            throw new EntityNotFoundException("Record with id '" + id + "' not found");
        }

        resultService.getResultsByRecordId(id)
                .forEach(result -> resultService.delete(result.getId()));
        delete(id);
    }

    /**
     * Updates a record with the given ID using the provided RecordUpdateDto.
     *
     * @param id     the ID of the record to update
     * @param record the RecordUpdateDto containing the new data
     * @return the updated Record
     */
    public Record update(String id, RecordUpdateDto record) {
        var rec = recordMapper.mapFromUpdateDto(record);
        recordDatabaseValidator.validate(rec);
        return recordService.update(id, rec);
    }

    /**
     * Saves a new record using the provided RecordUpdateDto.
     *
     * @param entity the RecordUpdateDto containing the data for the new record
     * @return the saved Record
     */
    public Record save(RecordUpdateDto entity) {
        var rec = recordMapper.mapFromUpdateDto(entity);
        recordDatabaseValidator.validate(rec);
        return recordService.save(rec);
    }
}
