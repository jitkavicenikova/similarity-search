package thesis.domain.manipulation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesis.data.mapper.ResultMapper;
import thesis.data.model.Result;
import thesis.data.service.RecordService;
import thesis.data.service.ResultService;
import thesis.data.validation.database.ResultDatabaseValidator;
import thesis.domain.manipulation.dto.ResultCreateDto;
import thesis.domain.manipulation.dto.ResultUpdateDto;

import java.util.List;

@Service
public class ResultManipulationService extends BaseEntityManipulationService<Result> {
    private final ResultService resultService;
    private final ResultDatabaseValidator resultDatabaseValidator;
    private final ResultMapper resultMapper;
    private final RecordService recordService;

    @Autowired
    public ResultManipulationService(ResultService resultService, ResultDatabaseValidator resultDatabaseValidator,
                                     RecordService recordService, ResultMapper resultMapper) {
        super(resultService, resultDatabaseValidator);
        this.resultService = resultService;
        this.resultDatabaseValidator = resultDatabaseValidator;
        this.recordService = recordService;
        this.resultMapper = resultMapper;
    }

    public List<Result> getResultsByRecordId(String recordId) {
        return resultService.getResultsByRecordId(recordId);
    }

    /**
     * Updates an existing result with the given ID using the provided ResultUpdateDto.
     *
     * @param id     the ID of the result to update
     * @param result the ResultUpdateDto containing the updated data
     * @return the updated Result
     */
    public Result update(String id, ResultUpdateDto result) {
        var res = resultMapper.mapFromUpdateDto(result);
        resultDatabaseValidator.validate(res);
        return resultService.update(id, res);
    }

    /**
     * Saves a new result using the provided ResultCreateDto.
     *
     * @param entity the ResultCreateDto containing the new data
     * @return the saved Result
     */
    public Result save(ResultCreateDto entity) {
        recordService.getEntity(entity.recordId());
        return save(resultMapper.mapFromCreateDto(entity));
    }
}
