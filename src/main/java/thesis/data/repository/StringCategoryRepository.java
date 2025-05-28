package thesis.data.repository;

import org.springframework.data.repository.CrudRepository;
import thesis.data.model.StringCategory;

public interface StringCategoryRepository extends CrudRepository<StringCategory, String> {
}