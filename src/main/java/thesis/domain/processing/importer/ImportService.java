package thesis.domain.processing.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import thesis.data.mapper.DataSetMapper;
import thesis.data.model.DataSet;
import thesis.domain.processing.dto.DataSetDto;
import thesis.domain.processing.transformer.TransformerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

/**
 * Importer class responsible for processing files, validating data, and saving it to the database.
 */
@Service
public class ImportService {
    private final TransformerService transformerService;
    private final DatabaseService databaseService;
    private final ValidationService validationService;
    private final DataSetMapper dataSetMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public ImportService(TransformerService transformerService, ObjectMapper objectMapper, DataSetMapper dataSetMapper,
                         DatabaseService databaseService, ValidationService validationService) {
        this.transformerService = transformerService;
        this.objectMapper = objectMapper;
        this.dataSetMapper = dataSetMapper;
        this.databaseService = databaseService;
        this.validationService = validationService;
    }

    /**
     * Transforms the given file into a DataSetDto and uploads it to the database.
     *
     * @param file      the file to be transformed
     * @param increment whether the upload is incremental
     * @throws IOException if an error occurs during file processing
     */
    public void transformAndUploadFile(MultipartFile file, boolean increment) throws IOException {
        DataSetDto dataSetDto = transformerService.transform(file, increment);
        processDataSet(dataSetDto, file.getOriginalFilename(), increment);
    }

    /**
     * Processes the given file, validates its content, and saves it to the database.
     *
     * @param file      the file to be processed
     * @param increment whether the upload is incremental
     * @throws IOException if an error occurs during file processing
     */
    public void processFile(MultipartFile file, boolean increment) throws IOException {
        DataSetDto dataSetDto = parseFile(file);
        processDataSet(dataSetDto, file.getOriginalFilename(), increment);
    }

    private void processDataSet(DataSetDto dataSetDto, String fileName, boolean increment) {
        if (dataSetDto == null) {
            throw new IllegalArgumentException("The uploaded file is empty or contains invalid data.");
        }

        var dataSet = dataSetMapper.mapToModel(dataSetDto);
        populateMetadata(dataSet, fileName);

        if (increment) {
            validationService.validateIncrement(dataSet);
        } else {
            validationService.validate(dataSet);
        }

        databaseService.saveDataSet(dataSet);
    }

    private DataSetDto parseFile(MultipartFile file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return objectMapper.readValue(br, DataSetDto.class);
        }
    }

    private void populateMetadata(DataSet dataSet, String fileName) {
        dataSet.setFileName(fileName);
        dataSet.setImportDate(LocalDateTime.now());
    }
}
