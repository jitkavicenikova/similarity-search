package thesis.data.repository.custom;

import thesis.data.model.DeviationRange;
import thesis.data.model.TechnologyProperties;

import java.util.List;
import java.util.Set;

/**
 * Custom repository interface for Technology entity.
 * This interface defines methods for saving, deleting, and searching technology properties in a Redis database.
 */
public interface CustomTechnologyRepository {
    /**
     * Saves isPercentage attribute to the database.
     *
     * @param technologyProperties the technology properties to save
     * @param technologyName       the name of the technology
     */
    void saveIsPercentage(TechnologyProperties technologyProperties, String technologyName);

    /**
     * Saves the sensitivity value for a specific technology and marker.
     *
     * @param technologyName the name of the technology
     * @param markerName     the name of the marker
     * @param sensitivity    the sensitivity value to save
     */
    void saveSensitivity(String technologyName, String markerName, Double sensitivity);

    /**
     * Saves the specificity value for a specific technology and marker.
     *
     * @param technologyName the name of the technology
     * @param markerName     the name of the marker
     * @param specificity    the specificity value to save
     */
    void saveSpecificity(String technologyName, String markerName, Double specificity);

    /**
     * Saves deviation ranges to the database.
     *
     * @param ranges          the list of deviation ranges to save
     * @param technologyName  the name of the technology
     * @param markerName      the name of the marker
     */
    void saveDeviationRanges(List<DeviationRange> ranges, String technologyName, String markerName);

    /**
     * Retrieves deviation for the given value based on lower limit of the interval.
     *
     * @param technologyName the name of the technology
     * @param markerName     the name of the marker
     * @param value          the value for which the deviation is searched
     * @return deviation identifier
     */
    Set<String> getFromDeviation(String technologyName, String markerName, Double value);

    /**
     * Retrieves deviation for the given value based on upper limit of the interval.
     *
     * @param technologyName the name of the technology
     * @param markerName     the name of the marker
     * @param value          the value for which the deviation is searched
     * @return deviation identifier
     */
    Set<String> getToDeviation(String technologyName, String markerName, Double value);

    /**
     * Checks if the deviation for a given technology and marker is stored as a percentage.
     *
     * @param technologyName the name of the technology
     * @param markerName     the name of the marker
     * @return true if the deviation is stored as a percentage, false otherwise, or null if not found
     */
    Boolean isDeviationPercentage(String technologyName, String markerName);

    /**
     * Searches for technologies with a minimum sensitivity value for a given marker.
     *
     * @param markerName   the name of the marker
     * @param sensitivity  the minimum sensitivity value
     * @return a set of technology names that meet the criteria
     */
    Set<String> searchForTechnologyWithMinSensitivity(String markerName, Double sensitivity);

    /**
     * Searches for technologies with a minimum specificity value for a given marker.
     *
     * @param markerName   the name of the marker
     * @param specificity  the minimum specificity value
     * @return a set of technology names that meet the criteria
     */
    Set<String> searchForTechnologyWithMinSpecificity(String markerName, Double specificity);

    /**
     * Deletes the deviation value for a specific technology and marker.
     *
     * @param markerName     the name of the marker
     * @param technologyName the name of the technology
     */
    void deleteDeviation(String markerName, String technologyName);

    /**
     * Deletes the sensitivity value for a specific technology and marker.
     *
     * @param markerName     the name of the marker
     * @param technologyName the name of the technology
     */
    void deleteSensitivity(String markerName, String technologyName);

    /**
     * Deletes the specificity value for a specific technology and marker.
     *
     * @param markerName     the name of the marker
     * @param technologyName the name of the technology
     */
    void deleteSpecificity(String markerName, String technologyName);

    /**
     * Deletes deviation ranges for a specific technology and marker.
     *
     * @param ranges          the list of deviation ranges to delete
     * @param markerName      the name of the marker
     * @param technologyName  the name of the technology
     */
    void deleteDeviationRange(List<DeviationRange> ranges, String markerName, String technologyName);
}
