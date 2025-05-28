package thesis.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import thesis.data.model.Record;
import thesis.domain.manipulation.dto.RecordUpdateDto;
import thesis.domain.processing.dto.RecordDto;

/**
 * Mapper for Record model
 */
@Mapper(componentModel = "spring")
public interface RecordMapper {
    @Mapping(target = "idRaw", source = "id")
    @Mapping(target = "id", ignore = true)
    Record mapToModel(RecordDto dto);

    @Mapping(target = "id", source = "idRaw")
    RecordDto mapToDto(Record model);

    Record mapFromUpdateDto(RecordUpdateDto dto);
}
