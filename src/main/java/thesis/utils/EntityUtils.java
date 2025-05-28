package thesis.utils;

import org.json.JSONObject;
import thesis.data.model.DataSet;
import thesis.data.model.DeviationRange;
import thesis.data.model.Record;

import java.util.Comparator;
import java.util.List;

/**
 * Utility class for handling entity-related operations.
 * This class provides methods to generate metadata, result IDs, and check for overlaps in deviation ranges.
 */
public final class EntityUtils {
    private EntityUtils() {
    }

    /**
     * Generates metadata for a given record.
     *
     * @param dataSet the data set containing metadata information
     * @param record  the record to enrich with metadata
     * @return a JSON string representing the metadata
     */
    public static String generateRecordMetadata(DataSet dataSet, Record record) {
        JSONObject metadataJson = record.getMetadata() == null ? new JSONObject()
                : new JSONObject(record.getMetadata());
        metadataJson.put("fileName", dataSet.getFileName());
        metadataJson.put("importDate", dataSet.getImportDate());
        return metadataJson.toString();
    }

    /**
     * Generates a result ID by concatenating the record ID and marker name.
     *
     * @param recordId   the record ID
     * @param markerName the marker name
     * @return the generated result ID
     */
    public static String generateResultId(String recordId, String markerName) {
        return recordId + ":" + markerName;
    }

    /**
     * Generates a deviation range ID by concatenating the from, to, and deviation values.
     *
     * @param deviation the deviation range
     * @return the generated deviation range ID
     */
    public static String generateDeviationRangeId(DeviationRange deviation) {
        return deviation.getFrom().toString() + "::" + deviation.getTo() + "::" + deviation.getDeviation().toString();
    }

    /**
     * Checks if the given list of deviation ranges contains any overlaps.
     *
     * @param ranges the list of deviation ranges to check
     * @return true if the list contains overlaps, false otherwise
     */
    public static boolean hasOverlaps(List<DeviationRange> ranges) {
        if (ranges == null || ranges.isEmpty()) {
            return false;
        }

        ranges.sort(Comparator.comparingDouble(DeviationRange::getFrom));

        for (int i = 1; i < ranges.size(); i++) {
            DeviationRange prev = ranges.get(i - 1);
            DeviationRange current = ranges.get(i);

            if (current.getFrom() < prev.getTo()) {
                return true;
            }
        }

        return false;
    }
}
