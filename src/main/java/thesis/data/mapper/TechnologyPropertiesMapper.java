package thesis.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import thesis.data.model.TechnologyProperties;
import thesis.domain.processing.dto.TechnologyPropertiesDto;

/**
 * Mapper for TechnologyProperties model
 */
@Mapper(componentModel = "spring", uses = DeviationRangeMapper.class)
public interface TechnologyPropertiesMapper {
    @Mapping(target = "deviationRanges", source = "deviationRanges")
    TechnologyProperties mapToModel(TechnologyPropertiesDto dto);

    @Mapping(target = "deviationRanges", source = "deviationRanges")
    TechnologyPropertiesDto mapToDto(TechnologyProperties model);
}