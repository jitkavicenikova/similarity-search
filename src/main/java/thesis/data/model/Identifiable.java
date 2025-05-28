package thesis.data.model;

/**
 * Interface for identifiable objects.
 */
@FunctionalInterface
public interface Identifiable {
    /**
     * Returns the unique identifier of the object.
     *
     * @return the unique identifier as a String
     */
    String getId();
}
