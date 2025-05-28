package thesis.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import thesis.data.model.Result;
import thesis.domain.manipulation.dto.ResultCreateDto;
import thesis.domain.manipulation.dto.ResultUpdateDto;
import thesis.domain.processing.dto.ResultDto;

/**
 * Mapper for Result model
 */
@Mapper(componentModel = "spring")
public interface ResultMapper {
    @Mapping(target = "recordIdRaw", source = "recordId")
    Result mapToModel(ResultDto dto);

    @Mapping(target = "recordId", source = "recordIdRaw")
    ResultDto mapToDto(Result model);

    @Mapping(target = "id", expression = "java(dto.recordId() + \":\" + dto.markerName())")
    Result mapFromCreateDto(ResultCreateDto dto);

    Result mapFromUpdateDto(ResultUpdateDto dto);
}