package thesis.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class for JSON operations.
 */
public final class JsonUtils {
    private JsonUtils() {
    }

    /**
     * Checks if the given string is a valid JSON object or array.
     *
     * @param json the string to check
     * @return true if the string is a valid JSON object or array, false otherwise
     */
    public static boolean isValidJson(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            try {
                new JSONArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
