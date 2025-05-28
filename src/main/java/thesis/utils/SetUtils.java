package thesis.utils;

import java.util.Set;

/**
 * Utility class for set operations.
 */
public final class SetUtils {
    private SetUtils() {
    }

    /**
     * Computes the intersection of two sets.
     *
     * @param set1 the first set, which will be modified to contain the intersection
     * @param set2 the second set
     * @return the modified first set containing the intersection of the two sets
     */
    public static Set<String> getIntersection(Set<String> set1, Set<String> set2) {
        set1.retainAll(set2);
        return set1;
    }
}
