package thesis.data.validation.base;

import thesis.data.model.TechnologyProperties;
import thesis.exceptions.ValidationException;
import thesis.utils.EntityUtils;

import java.util.HashSet;
import java.util.Set;

public abstract class TechnologyPropertiesBaseValidator implements BaseValidator<TechnologyProperties> {
    @Override
    public void validate(TechnologyProperties properties) {
        if (properties.getDeviationRanges() != null && properties.getDeviationRanges().isEmpty()) {
            throw new ValidationException("Deviation ranges must be null or not empty", properties);
        }

        if (properties.getComparableWith() != null && properties.getComparableWith().isEmpty()) {
            throw new ValidationException("Comparable with must be null or not empty", properties);
        }

        if (properties.getDeviationRanges() != null && properties.getIsPercentage() == null) {
            throw new ValidationException("Deviation ranges are defined but isPercentage is not set", properties);
        }

        if (properties.getDeviationRanges() == null && properties.getIsPercentage() != null) {
            throw new ValidationException("isPercentage is set but deviation ranges are not defined", properties);
        }

        if (properties.getDeviationRanges() != null && EntityUtils.hasOverlaps(properties.getDeviationRanges())) {
            throw new ValidationException("Deviations contain overlapping ranges", properties);
        }

        if (properties.getDeviationRanges() == null && properties.getComparableWith() == null
                && properties.getSensitivity() == null && properties.getSpecificity() == null) {
            throw new ValidationException("At least one of deviation ranges, sensitivity, specificity, or comparableWith must be set", properties);
        }

        if (properties.getComparableWith() != null) {
            Set<String> uniqueValues = new HashSet<>();
            for (String value : properties.getComparableWith()) {
                if (value == null || value.isBlank()) {
                    throw new ValidationException("comparableWith contains null or empty values", properties);
                }
                if (!uniqueValues.add(value)) {
                    throw new ValidationException("comparableWith contains duplicates", properties);
                }
            }
        }
    }
}
