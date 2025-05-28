package thesis.domain.manipulation.service;

import thesis.data.model.Identifiable;
import thesis.data.service.EntityService;
import thesis.data.validation.database.DatabaseValidator;

/**
 * Base class for entity manipulation services.
 *
 * @param <T> the type of entity to manipulate
 */
public abstract class BaseEntityManipulationService<T extends Identifiable> implements EntityManipulationService<T> {
    private final EntityService<T> entityService;
    private final DatabaseValidator<T> databaseValidator;

    public BaseEntityManipulationService(EntityService<T> entityService, DatabaseValidator<T> databaseValidator) {
        this.entityService = entityService;
        this.databaseValidator = databaseValidator;
    }

    @Override
    public T save(T entity) {
        if (entityService.existsById(entity.getId())) {
            throw new IllegalArgumentException("Entity with id '" + entity.getId() + "' already exists");
        }
        databaseValidator.runValidation(entity);
        return entityService.save(entity);
    }

    @Override
    public T getEntity(String id) {
        return entityService.getEntity(id);
    }

    @Override
    public void delete(String id) {
        entityService.delete(id);
    }

    @Override
    public Iterable<T> findAll() {
        return entityService.findAll();
    }
}
