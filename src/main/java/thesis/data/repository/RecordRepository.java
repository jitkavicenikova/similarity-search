package thesis.data.repository;

import org.springframework.data.repository.CrudRepository;
import thesis.data.model.Record;

public interface RecordRepository extends CrudRepository<Record, String> {
}
