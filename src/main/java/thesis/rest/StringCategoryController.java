package thesis.rest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import thesis.data.model.StringCategory;
import thesis.domain.manipulation.service.StringCategoryManipulationService;
import thesis.utils.JsonLog;

@RestController
@RequestMapping("/stringCategory")
public class StringCategoryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringCategoryController.class);
    private final StringCategoryManipulationService stringCategoryService;

    @Autowired
    public StringCategoryController(StringCategoryManipulationService stringCategoryService) {
        this.stringCategoryService = stringCategoryService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public StringCategory create(@Valid @RequestBody StringCategory stringCategory) {
        LOGGER.info("Processing create category: {}", JsonLog.toJson(stringCategory));
        return stringCategoryService.save(stringCategory);
    }

    @GetMapping("/{name}")
    public StringCategory get(@PathVariable("name") String name) {
        LOGGER.info("Processing get category: {}", name);
        return stringCategoryService.getEntity(name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{name}")
    public void delete(@PathVariable("name") String name) {
        LOGGER.info("Processing delete category: {}", name);
        stringCategoryService.delete(name);
    }

    @PostMapping("/value/{name}")
    public StringCategory addValue(@PathVariable("name") String name, @Valid @RequestBody String value) {
        LOGGER.info("Processing add value to category: {} with value: {}", name, value);
        return stringCategoryService.addValue(name, value);
    }

    @GetMapping("all")
    public Iterable<StringCategory> getAll() {
        LOGGER.info("Processing get all categories");
        return stringCategoryService.findAll();
    }
}
