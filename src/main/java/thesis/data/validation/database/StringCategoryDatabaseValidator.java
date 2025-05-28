package thesis.data.validation.database;

import org.springframework.stereotype.Component;
import thesis.data.model.StringCategory;
import thesis.data.validation.base.StringCategoryBaseValidator;

@Component
public class StringCategoryDatabaseValidator extends StringCategoryBaseValidator implements DatabaseValidator<StringCategory> {
    @Override
    public void validateEntity(StringCategory category) {
        validate(category);
    }
}
