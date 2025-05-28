package thesis.domain.manipulation.service;

/**
 * Interface for entity manipulation services.
 *
 * @param <T> the type of entity to manipulate
 */
public interface EntityManipulationService<T> {
    T save(T entity);

    T getEntity(String id);

    void delete(String id);

    Iterable<T> findAll();
}
