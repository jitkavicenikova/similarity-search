package thesis.rest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import thesis.data.model.Technology;
import thesis.data.model.TechnologyProperties;
import thesis.domain.manipulation.service.TechnologyManipulationService;
import thesis.utils.JsonLog;

@RestController
@RequestMapping("/technology")
public class TechnologyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TechnologyController.class);
    private final TechnologyManipulationService technologyService;

    @Autowired
    public TechnologyController(TechnologyManipulationService technologyService) {
        this.technologyService = technologyService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Technology create(@Valid @RequestBody Technology technology) {
        LOGGER.info("Processing create technology: {}", JsonLog.toJson(technology));
        return technologyService.save(technology);
    }

    @GetMapping("/{name}")
    public Technology get(@PathVariable("name") String name) {
        LOGGER.info("Processing get technology: {}", name);
        return technologyService.getEntity(name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{name}")
    public void delete(@PathVariable("name") String name) {
        LOGGER.info("Processing delete technology: {}", name);
        technologyService.delete(name);
    }

    @PostMapping("/properties/{name}")
    public Technology addProperties(@PathVariable("name") String name, @Valid @RequestBody TechnologyProperties technologyProperties) {
        LOGGER.info("Processing add properties to technology: {} with properties: {}", name, JsonLog.toJson(technologyProperties));
        return technologyService.addProperties(name, technologyProperties);
    }

    @GetMapping("all")
    public Iterable<Technology> getAll() {
        LOGGER.info("Processing get all technologies");
        return technologyService.findAll();
    }
}
