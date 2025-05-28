package thesis.data.mapper;

import org.mapstruct.Mapper;
import thesis.data.model.Conversion;
import thesis.domain.processing.dto.ConversionDto;

/**
 * Mapper for Conversion model
 */
@Mapper(componentModel = "spring")
public interface ConversionMapper {
    Conversion mapToModel(ConversionDto dto);

    ConversionDto mapToDto(Conversion model);
}
