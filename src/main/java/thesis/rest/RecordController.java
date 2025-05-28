package thesis.rest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import thesis.data.model.Record;
import thesis.domain.manipulation.dto.RecordUpdateDto;
import thesis.domain.manipulation.service.RecordManipulationService;
import thesis.utils.JsonLog;

@RestController
@RequestMapping("/record")
public class RecordController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordController.class);
    private final RecordManipulationService recordService;

    @Autowired
    public RecordController(RecordManipulationService recordService) {
        this.recordService = recordService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Record create(@Valid @RequestBody RecordUpdateDto record) {
        LOGGER.info("Processing create record: {}", JsonLog.toJson(record));
        return recordService.save(record);
    }

    @GetMapping("/{id}")
    public Record get(@PathVariable("id") String id) {
        LOGGER.info("Processing get record: {}", id);
        return recordService.getEntity(id);
    }

    @PatchMapping("/{id}")
    public Record update(@PathVariable("id") String id, @Valid @RequestBody RecordUpdateDto record) {
        LOGGER.info("Processing update record: {}", JsonLog.toJson(record));
        return recordService.update(id, record);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        LOGGER.info("Processing delete record: {}", id);
        recordService.delete(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/including-results/{id}")
    public void deleteWithResults(@PathVariable("id") String id) {
        LOGGER.info("Processing delete record with results: {}", id);
        recordService.deleteWithResults(id);
    }

    @GetMapping("all")
    public Iterable<Record> getAll() {
        LOGGER.info("Processing get all records");
        return recordService.findAll();
    }
}
