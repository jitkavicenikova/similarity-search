package thesis.data.repository;

import org.springframework.data.repository.CrudRepository;
import thesis.data.model.Unit;

public interface UnitRepository extends CrudRepository<Unit, String> {
}
