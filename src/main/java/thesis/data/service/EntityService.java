package thesis.data.service;

import thesis.data.model.Identifiable;

/**
 * Interface for entity services.
 * <p>
 * This interface defines methods for managing entities, including saving, retrieving,
 * deleting, and checking existence by ID.
 * </p>
 *
 * @param <T> the type of entity
 */
public interface EntityService<T extends Identifiable> {
    T save(T entity);

    T getEntity(String id);

    void delete(String id);

    Iterable<T> findAll();

    Boolean existsById(String id);
}
