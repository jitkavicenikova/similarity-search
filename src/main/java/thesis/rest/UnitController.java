package thesis.rest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import thesis.data.model.Conversion;
import thesis.data.model.Unit;
import thesis.domain.manipulation.service.UnitManipulationService;
import thesis.utils.JsonLog;

@RestController
@RequestMapping("/unit")
public class UnitController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnitController.class);
    private final UnitManipulationService unitService;

    @Autowired
    public UnitController(UnitManipulationService unitService) {
        this.unitService = unitService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Unit create(@Valid @RequestBody Unit unit) {
        LOGGER.info("Processing create unit: {}", JsonLog.toJson(unit));
        return unitService.save(unit);
    }

    @GetMapping("/{name}")
    public Unit get(@PathVariable("name") String name) {
        LOGGER.info("Processing get unit: {}", name);
        return unitService.getEntity(name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{name}")
    public void delete(@PathVariable("name") String name) {
        LOGGER.info("Processing delete unit: {}", name);
        unitService.delete(name);
    }

    @PostMapping("/conversion/{name}")
    public Unit addConversion(@PathVariable("name") String name, @Valid @RequestBody Conversion conversion) {
        LOGGER.info("Processing add conversion to unit: {} with conversion: {}", name, JsonLog.toJson(conversion));
        return unitService.addConversion(name, conversion);
    }

    @GetMapping("all")
    public Iterable<Unit> getAll() {
        LOGGER.info("Processing get all units");
        return unitService.findAll();
    }
}