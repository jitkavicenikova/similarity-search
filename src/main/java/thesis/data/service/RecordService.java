package thesis.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.Record;
import thesis.data.repository.RecordRepository;
import thesis.data.repository.ResultRepository;
import thesis.exceptions.EntityInUseException;
import thesis.exceptions.EntityNotFoundException;

@Service
public class RecordService extends BaseEntityService<Record> {
    private final RecordRepository recordRepository;
    private final ResultRepository resultRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository, ResultRepository resultRepository) {
        super(recordRepository);
        this.recordRepository = recordRepository;
        this.resultRepository = resultRepository;
    }

    @Override
    public Record save(Record record) {
        return recordRepository.save(record);
    }

    @Override
    public Record getEntity(String id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id '" + id + "' not found"));
    }

    public Record update(String id, Record record) {
        var dbRecord = getEntity(id);

        dbRecord.setMetadata(record.getMetadata());

        return recordRepository.save(dbRecord);
    }

    @Override
    public void delete(String id) {
        if (!recordRepository.existsById(id)) {
            throw new EntityNotFoundException("Record with id '" + id + "' not found");
        }

        if (isRecordUsedInResults(id)) {
            throw new EntityInUseException("Cannot delete record with id '" + id + "' because it is used in results");
        }

        recordRepository.deleteById(id);
    }

    private Boolean isRecordUsedInResults(String recordId) {
        return resultRepository.existsByRecordId(recordId);
    }
}
