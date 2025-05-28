package thesis.domain.processing.transformer.dataset;

import org.springframework.web.multipart.MultipartFile;
import thesis.domain.processing.dto.DataSetDto;

import java.io.IOException;

/**
 * Interface for importing data from files.
 */
public interface DataTransformer {
    /**
     * Reads data from the specified file and returns it as a DataSetDto.
     *
     * @param file the file to read data from
     * @return the DataSet containing the data read from the file
     */
    DataSetDto transformFull(MultipartFile file) throws IOException;

    /**
     * Reads result and records from the specified file and returns it as a DataSetDto.
     *
     * @param file the file to read data from
     * @return the DataSet containing the data read from the file
     */
    DataSetDto transformIncrement(MultipartFile file) throws IOException;
}
