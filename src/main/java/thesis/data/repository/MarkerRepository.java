package thesis.data.repository;

import org.springframework.data.repository.CrudRepository;
import thesis.data.model.Marker;

public interface MarkerRepository extends CrudRepository<Marker, String> {
    Boolean existsByUnitName(String unitName);
}