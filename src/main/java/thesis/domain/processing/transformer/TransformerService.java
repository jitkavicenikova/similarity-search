package thesis.domain.processing.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import thesis.domain.processing.dto.DataSetDto;
import thesis.domain.processing.transformer.dataset.DataTransformer;

import java.io.IOException;

/**
 * The Extractor class is responsible for processing data and exporting it to unified JSON format.
 */
@Service
public class TransformerService {
    private final DataTransformer transformer;
    private final ObjectMapper objectMapper;

    public TransformerService(ObjectMapper objectMapper, DataTransformer transformer) {
        this.transformer = transformer;
        this.objectMapper = objectMapper;
    }

    /**
     * Transforms the given file into a DataSetDto.
     *
     * @param file      the file to be transformed
     * @param increment whether the upload is incremental
     * @return the transformed DataSetDto
     * @throws IOException if an error occurs during file processing
     */
    public DataSetDto transform(MultipartFile file, Boolean increment) throws IOException {
        if (increment) {
            return transformer.transformIncrement(file);
        }

        return transformer.transformFull(file);
    }

    /**
     * Transforms the given file into a JSON string.
     *
     * @param file      the file to be transformed
     * @param increment whether the upload is incremental
     * @return the transformed JSON string
     * @throws IOException if an error occurs during file processing
     */
    public String transformAndProcess(MultipartFile file, Boolean increment) throws IOException {
        return exportToJson(transform(file, increment));
    }

    private String exportToJson(DataSetDto dataSet) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dataSet);
    }
}
