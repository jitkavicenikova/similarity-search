package thesis.domain.processing.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import thesis.data.mapper.DataSetMapper;
import thesis.data.model.DataSet;
import thesis.domain.processing.dto.DataSetDto;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImportServiceTest {
    @InjectMocks
    private ImportService importService;

    @Mock
    private DatabaseService databaseService;

    @Mock
    private ValidationService validationService;

    @Mock
    private DataSetMapper dataSetMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MultipartFile file;

    private DataSetDto dataSetDto;
    private DataSet dataSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dataSetDto = new DataSetDto();
        dataSet = new DataSet();
    }

    @Test
    void processFile_ShouldProcessAndSaveDataSet_WhenValidFileIsUploaded() throws IOException {
        byte[] fileContent = "{ \"sampleData\": \"example\" }".getBytes();
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));
        when(objectMapper.readValue(any(BufferedReader.class), eq(DataSetDto.class))).thenReturn(dataSetDto);
        when(dataSetMapper.mapToModel(dataSetDto)).thenReturn(dataSet);
        when(file.getOriginalFilename()).thenReturn("testFile.json");

        importService.processFile(file, false);

        assertEquals("testFile.json", dataSet.getFileName());
        assertNotNull(dataSet.getImportDate());
        verify(validationService).validate(dataSet);
        verify(databaseService).saveDataSet(dataSet);
    }

    @Test
    void processFile_ShouldValidateIncrement_WhenIncrementIsTrue() throws IOException {
        byte[] fileContent = "{ \"sampleData\": \"example\" }".getBytes();
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));
        when(objectMapper.readValue(any(BufferedReader.class), eq(DataSetDto.class))).thenReturn(dataSetDto);
        when(dataSetMapper.mapToModel(dataSetDto)).thenReturn(dataSet);
        when(file.getOriginalFilename()).thenReturn("incrementFile.json");

        importService.processFile(file, true);

        verify(validationService).validateIncrement(dataSet);
        verify(databaseService).saveDataSet(dataSet);
    }

    @Test
    void processFile_ShouldThrowException_WhenFileIsEmpty() throws IOException {
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(objectMapper.readValue(any(BufferedReader.class), eq(DataSetDto.class))).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> importService.processFile(file, false));

        assertEquals("The uploaded file is empty or contains invalid data.", exception.getMessage());
        verifyNoInteractions(validationService, databaseService);
    }

    @Test
    void processFile_ShouldHandleIOException_WhenParsingFails() throws IOException {
        when(file.getInputStream()).thenThrow(new IOException("I/O error"));

        IOException exception = assertThrows(IOException.class, () -> importService.processFile(file, false));
        assertEquals("I/O error", exception.getMessage());
        verifyNoInteractions(validationService, databaseService);
    }
}
