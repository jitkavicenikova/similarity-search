package thesis.data.mapper;

import org.mapstruct.Mapper;
import thesis.data.model.DataSet;
import thesis.domain.processing.dto.DataSetDto;

/**
 * Mapper for DataSet model
 */
@Mapper(componentModel = "spring",
        uses = {MarkerMapper.class, UnitMapper.class,
                RecordMapper.class, ResultMapper.class,
                TechnologyMapper.class, StringCategoryMapper.class})
public interface DataSetMapper {
    DataSet mapToModel(DataSetDto dto);

    DataSetDto mapToDto(DataSet model);
}
