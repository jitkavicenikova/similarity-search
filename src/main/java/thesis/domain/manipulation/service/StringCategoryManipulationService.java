package thesis.domain.manipulation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.StringCategory;
import thesis.data.service.StringCategoryService;
import thesis.data.validation.database.StringCategoryDatabaseValidator;

@Service
public class StringCategoryManipulationService extends BaseEntityManipulationService<StringCategory> {
    private final StringCategoryService stringCategoryService;

    @Autowired
    public StringCategoryManipulationService(StringCategoryService stringCategoryService, StringCategoryDatabaseValidator categoryDatabaseValidator) {
        super(stringCategoryService, categoryDatabaseValidator);
        this.stringCategoryService = stringCategoryService;
    }

    public StringCategory addValue(String categoryName, String value) {
        return stringCategoryService.addValue(categoryName, value);
    }
}
