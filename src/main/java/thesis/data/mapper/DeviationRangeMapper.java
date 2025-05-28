package thesis.data.mapper;

import org.mapstruct.Mapper;
import thesis.data.model.DeviationRange;
import thesis.domain.processing.dto.DeviationRangeDto;

/**
 * Mapper for DeviationRange model
 */
@Mapper(componentModel = "spring")
public interface DeviationRangeMapper {
    DeviationRange mapToModel(DeviationRangeDto dto);

    DeviationRangeDto mapToDto(DeviationRange model);
}
