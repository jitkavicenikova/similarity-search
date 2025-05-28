package thesis.data.mapper;

import org.mapstruct.Mapper;
import thesis.data.model.Technology;
import thesis.domain.processing.dto.TechnologyDto;

/**
 * Mapper for Technology model
 */
@Mapper(componentModel = "spring", uses = {TechnologyPropertiesMapper.class})
public interface TechnologyMapper {
    Technology mapToModel(TechnologyDto dto);

    TechnologyDto mapToDto(Technology model);
}
