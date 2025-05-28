package thesis.domain.manipulation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thesis.data.mapper.ResultMapper;
import thesis.data.model.Record;
import thesis.data.model.Result;
import thesis.data.service.RecordService;
import thesis.data.service.ResultService;
import thesis.data.validation.database.ResultDatabaseValidator;
import thesis.domain.manipulation.dto.ResultCreateDto;
import thesis.domain.manipulation.dto.ResultUpdateDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResultManipulationServiceTest {

    @Mock
    private ResultService resultService;

    @Mock
    private ResultDatabaseValidator resultDatabaseValidator;

    @Mock
    private RecordService recordService;

    @Mock
    private ResultMapper resultMapper;

    @InjectMocks
    private ResultManipulationService resultManipulationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getResultsByRecordId_ShouldReturnResults_WhenRecordExists() {
        String recordId = "recordId123";
        Result result1 = new Result();
        result1.setId("result1");
        Result result2 = new Result();
        result2.setId("result2");

        when(resultService.getResultsByRecordId(recordId)).thenReturn(List.of(result1, result2));

        List<Result> results = resultManipulationService.getResultsByRecordId(recordId);

        assertEquals(2, results.size());
        assertEquals("result1", results.get(0).getId());
        assertEquals("result2", results.get(1).getId());
        verify(resultService).getResultsByRecordId(recordId);
    }

    @Test
    void update_ShouldCallResultServiceUpdate_WhenValidData() {
        String id = "result123";
        var resultUpdateDto = new ResultUpdateDto(1,
                1.0, 1.0, null, null, null,
                null, null, null, null, null, null);
        Result updatedResult = new Result();
        updatedResult.setId(id);

        when(resultMapper.mapFromUpdateDto(resultUpdateDto)).thenReturn(updatedResult);
        when(resultService.update(id, updatedResult)).thenReturn(updatedResult);

        Result result = resultManipulationService.update(id, resultUpdateDto);

        verify(resultMapper).mapFromUpdateDto(resultUpdateDto);
        verify(resultService).update(id, updatedResult);
        assertEquals(id, result.getId());
    }

    @Test
    void save_ShouldCallResultServiceSave_WhenValidData() {
        ResultCreateDto resultCreateDto = new ResultCreateDto("record123", "marker", 1,
                1.0, 1.0, null, null, null,
                null, null, null, null, null, null);
        Result result = new Result();
        result.setId("newResultId");

        Record record = new Record();
        record.setId("record123");

        when(recordService.getEntity(resultCreateDto.recordId())).thenReturn(record);
        when(resultMapper.mapFromCreateDto(resultCreateDto)).thenReturn(result);
        when(resultService.save(result)).thenReturn(result);

        Result savedResult = resultManipulationService.save(resultCreateDto);

        verify(recordService).getEntity(resultCreateDto.recordId());
        verify(resultMapper).mapFromCreateDto(resultCreateDto);
        verify(resultService).save(result);
        assertEquals("newResultId", savedResult.getId());
    }
}
