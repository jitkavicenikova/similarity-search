package thesis.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import thesis.data.model.Marker;
import thesis.domain.manipulation.service.MarkerManipulationService;
import thesis.utils.JsonLog;

@RestController
@RequestMapping("/marker")
public class MarkerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarkerController.class);
    private final MarkerManipulationService markerService;

    @Autowired
    public MarkerController(MarkerManipulationService markerService) {
        this.markerService = markerService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Marker create(@RequestBody Marker marker) {
        LOGGER.info("Processing create marker: {}", JsonLog.toJson(marker));
        return markerService.save(marker);
    }

    @GetMapping("/{name}")
    public Marker get(@PathVariable("name") String name) {
        LOGGER.info("Processing get marker: {}", name);
        return markerService.getEntity(name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{name}")
    public void delete(@PathVariable("name") String name) {
        LOGGER.info("Processing delete marker: {}", name);
        markerService.delete(name);
    }

    @GetMapping("all")
    public Iterable<Marker> getAll() {
        LOGGER.info("Processing get all markers");
        return markerService.findAll();
    }
}
