package thesis.data.service;

import org.springframework.data.repository.CrudRepository;
import thesis.data.model.Identifiable;

/**
 * Base service class for managing entities.
 *
 * @param <T> the type of entity
 */
public abstract class BaseEntityService<T extends Identifiable> implements EntityService<T> {
    protected final CrudRepository<T, String> repository;

    public BaseEntityService(CrudRepository<T, String> repository) {
        this.repository = repository;
    }

    @Override
    public T save(T entity) {
        if (!repository.existsById(entity.getId())) {
            return repository.save(entity);
        }

        return getEntity(entity.getId());
    }

    @Override
    public Iterable<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Boolean existsById(String id) {
        return repository.existsById(id);
    }
}
