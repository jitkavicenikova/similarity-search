package thesis.data.mapper;

import org.mapstruct.Mapper;
import thesis.data.model.StringCategory;
import thesis.domain.processing.dto.StringCategoryDto;

@Mapper(componentModel = "spring")
public interface StringCategoryMapper {
    StringCategory mapToModel(StringCategoryDto dto);

    StringCategoryDto mapToDto(StringCategory model);
}
