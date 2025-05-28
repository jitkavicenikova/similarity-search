package thesis.data.mapper;

import org.mapstruct.Mapper;
import thesis.data.model.Marker;
import thesis.domain.processing.dto.MarkerDto;

/**
 * Mapper for Marker model
 */
@Mapper(componentModel = "spring")
public interface MarkerMapper {
    Marker mapToModel(MarkerDto dto);

    MarkerDto mapToDto(Marker model);
}
