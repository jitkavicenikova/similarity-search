package thesis.rest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import thesis.data.model.Result;
import thesis.domain.manipulation.dto.ResultCreateDto;
import thesis.domain.manipulation.dto.ResultUpdateDto;
import thesis.domain.manipulation.service.ResultManipulationService;
import thesis.utils.JsonLog;

import java.util.List;

@RestController
@RequestMapping("/result")
public class ResultController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultController.class);
    private final ResultManipulationService resultService;

    @Autowired
    public ResultController(ResultManipulationService resultService) {
        this.resultService = resultService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Result create(@Valid @RequestBody ResultCreateDto result) {
        LOGGER.info("Processing create result: {}", JsonLog.toJson(result));
        return resultService.save(result);
    }

    @GetMapping("/{id}")
    public Result get(@PathVariable("id") String id) {
        LOGGER.info("Processing get result: {}", id);
        return resultService.getEntity(id);
    }

    @PatchMapping("/{id}")
    public Result update(@PathVariable("id") String id, @Valid @RequestBody ResultUpdateDto result) {
        LOGGER.info("Processing update result: {}", JsonLog.toJson(result));
        return resultService.update(id, result);
    }

    @GetMapping("/record-id/{recordId}")
    public List<Result> getByRecordId(@PathVariable("recordId") String recordId) {
        LOGGER.info("Processing get results by record id: {}", recordId);
        return resultService.getResultsByRecordId(recordId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        LOGGER.info("Processing delete result: {}", id);
        resultService.delete(id);
    }

    @GetMapping("all")
    public Iterable<Result> getAll() {
        LOGGER.info("Processing get all results");
        return resultService.findAll();
    }
}
