package thesis.data.validation.dataset;

import org.springframework.stereotype.Component;
import thesis.data.model.DataSet;
import thesis.data.model.StringCategory;
import thesis.data.validation.base.StringCategoryBaseValidator;

@Component
public class StringCategoryDataSetValidator extends StringCategoryBaseValidator implements DataSetValidator<StringCategory> {
    @Override
    public void validateEntity(DataSet dataSet, StringCategory category) {
        validate(category);
    }
}
