package thesis.data.repository;

import org.springframework.data.repository.CrudRepository;
import thesis.data.model.Technology;
import thesis.data.repository.custom.CustomTechnologyRepository;

public interface TechnologyRepository extends CrudRepository<Technology, String>, CustomTechnologyRepository {
}
