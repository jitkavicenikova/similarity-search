package thesis.data.validation.base;

import thesis.data.model.StringCategory;
import thesis.exceptions.ValidationException;

import java.util.HashSet;
import java.util.Set;

public abstract class StringCategoryBaseValidator implements BaseValidator<StringCategory> {
    @Override
    public void validate(StringCategory category) {
        if (category.getValues() != null) {
            Set<String> uniqueValues = new HashSet<>();
            for (String value : category.getValues()) {
                if (value == null || value.isBlank()) {
                    throw new ValidationException("values contain null or empty values", category);
                }
                if (!uniqueValues.add(value)) {
                    throw new ValidationException("Duplicate values found in string category values", category);
                }
            }
        }
    }
}
