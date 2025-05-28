package thesis.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import thesis.domain.processing.importer.ImportService;

import java.io.IOException;

@RestController
@RequestMapping("/import")
public class ImportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportController.class);
    private final ImportService importService;

    @Autowired
    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value = "/full", consumes = "multipart/form-data")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        LOGGER.info("Processing uploaded file: {}", file.getOriginalFilename());
        importService.processFile(file, false);
    }

    @PostMapping(value = "/increment", consumes = "multipart/form-data")
    public void uploadIncrement(@RequestParam("file") MultipartFile file) throws IOException {
        LOGGER.info("Processing uploaded increment: {}", file.getOriginalFilename());
        importService.processFile(file, true);
    }

    @PostMapping(value = "/full-with-transform", consumes = "multipart/form-data")
    public void transformAndUploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        LOGGER.info("Transforming and processing uploaded file: {}", file.getOriginalFilename());
        importService.transformAndUploadFile(file, false);
    }

    @PostMapping(value = "/increment-with-transform", consumes = "multipart/form-data")
    public void transformAndUploadIncrement(@RequestParam("file") MultipartFile file) throws IOException {
        LOGGER.info("Transforming and processing uploaded increment: {}", file.getOriginalFilename());
        importService.transformAndUploadFile(file, true);
    }
}