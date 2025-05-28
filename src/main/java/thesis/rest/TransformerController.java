package thesis.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import thesis.domain.processing.transformer.TransformerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/transformer")
public class TransformerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformerController.class);
    private final TransformerService transformerService;

    @Autowired
    public TransformerController(TransformerService transformerService) {
        this.transformerService = transformerService;
    }

    @PostMapping(value = "/full", consumes = "multipart/form-data")
    public ResponseEntity<InputStreamResource> transformFile(@RequestParam("file") MultipartFile file) throws IOException {
        LOGGER.info("Processing transform file: {}", file.getOriginalFilename());
        var jsonContent = transformerService.transformAndProcess(file, false);
        return createResponseEntity(jsonContent);
    }

    @PostMapping(value = "/increment", consumes = "multipart/form-data")
    public ResponseEntity<InputStreamResource> transformIncrement(@RequestParam("file") MultipartFile file) throws IOException {
        LOGGER.info("Processing transform increment: {}", file.getOriginalFilename());
        var jsonContent = transformerService.transformAndProcess(file, true);
        return createResponseEntity(jsonContent);
    }

    private ResponseEntity<InputStreamResource> createResponseEntity(String jsonContent) {
        var inputStream = new ByteArrayInputStream(jsonContent.getBytes());
        var jsonBytes = jsonContent.getBytes(StandardCharsets.UTF_8);
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=processedData.json");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(jsonBytes.length)
                .body(new InputStreamResource(inputStream));
    }
}

