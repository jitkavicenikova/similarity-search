package thesis.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.model.StringCategory;
import thesis.data.repository.ResultRepository;
import thesis.data.repository.StringCategoryRepository;
import thesis.exceptions.BadRequestException;
import thesis.exceptions.EntityInUseException;
import thesis.exceptions.EntityNotFoundException;

@Service
public class StringCategoryService extends BaseEntityService<StringCategory> {
    private final StringCategoryRepository stringCategoryRepository;
    private final ResultRepository resultRepository;

    @Autowired
    public StringCategoryService(StringCategoryRepository stringCategoryRepository, ResultRepository resultRepository) {
        super(stringCategoryRepository);
        this.stringCategoryRepository = stringCategoryRepository;
        this.resultRepository = resultRepository;
    }

    @Override
    public StringCategory getEntity(String categoryName) {
        return stringCategoryRepository.findById(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("String category with name '" + categoryName + "' not found"));
    }

    @Override
    public void delete(String categoryName) {
        if (!stringCategoryRepository.existsById(categoryName)) {
            throw new EntityNotFoundException("String category with name '" + categoryName + "' not found");
        }

        if (isStringCategoryUsedInResults(categoryName)) {
            throw new EntityInUseException("Cannot delete category '" + categoryName + "' because it is used in results");
        }

        stringCategoryRepository.deleteById(categoryName);
    }

    public StringCategory addValue(String categoryName, String value) {
        var category = getEntity(categoryName);
        if (category.getIsComparable()) {
            throw new BadRequestException("Cannot add value to comparable category '" + categoryName + "'");
        }

        if (category.getValues().contains(value)) {
            throw new BadRequestException("Value '" + value + "' already exists in category '" + categoryName + "'");
        }

        category.getValues().add(value);
        return stringCategoryRepository.save(category);
    }

    private Boolean isStringCategoryUsedInResults(String categoryName) {
        return resultRepository.existsByStringValueCategory(categoryName);
    }
}
