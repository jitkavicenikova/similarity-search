package thesis.data.mapper;

import org.mapstruct.Mapper;
import thesis.data.model.Unit;
import thesis.domain.processing.dto.UnitDto;

/**
 * Mapper for Unit model
 */
@Mapper(componentModel = "spring", uses = {ConversionMapper.class})
public interface UnitMapper {
    Unit mapToModel(UnitDto dto);

    UnitDto mapToDto(Unit model);
}
