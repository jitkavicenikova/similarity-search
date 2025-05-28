package thesis.utils;

public class RedisKeyBuilder {
    private RedisKeyBuilder() {
    }

    public static String resultMinKey(String markerName) {
        return "marker:" + markerName + ":result:min";
    }

    public static String resultMaxKey(String markerName) {
        return "marker:" + markerName + ":result:max";
    }

    public static String techResultMinKey(String markerName) {
        return "marker:" + markerName + ":technology:result:min";
    }

    public static String techResultMaxKey(String markerName) {
        return "marker:" + markerName + ":technology:result:max";
    }

    public static String resultHashKey(String recordId, String markerName) {
        return "result:" + EntityUtils.generateResultId(recordId, markerName);
    }

    public static String sensitivityKey(String markerName) {
        return "marker:" + markerName + ":sensitivity";
    }

    public static String specificityKey(String markerName) {
        return "marker:" + markerName + ":specificity";
    }

    public static String technologyMarkerKey(String technologyName, String markerName) {
        return "technology:" + technologyName + ":marker:" + markerName;
    }

    public static String deviationRangeFromKey(String technologyName, String markerName) {
        return technologyMarkerKey(technologyName, markerName) + ":range:from";
    }

    public static String deviationRangeToKey(String technologyName, String markerName) {
        return technologyMarkerKey(technologyName, markerName) + ":range:to";
    }
}
