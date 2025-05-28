package thesis.domain.processing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a data set containing various elements such as markers, records, results, technologies, and units.
 */
public class DataSetDto {
    private String fileName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime importDate;
    private List<MarkerDto> markers;
    private List<RecordDto> records;
    private List<ResultDto> results;
    private List<TechnologyDto> technologies;
    private List<UnitDto> units;
    private List<StringCategoryDto> stringCategories;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDateTime importDate) {
        this.importDate = importDate;
    }

    public List<MarkerDto> getMarkers() {
        return markers;
    }

    public void setMarkers(List<MarkerDto> markers) {
        this.markers = markers;
    }

    public List<RecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<RecordDto> records) {
        this.records = records;
    }

    public List<ResultDto> getResults() {
        return results;
    }

    public void setResults(List<ResultDto> results) {
        this.results = results;
    }

    public List<TechnologyDto> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<TechnologyDto> technologies) {
        this.technologies = technologies;
    }

    public List<UnitDto> getUnits() {
        return units;
    }

    public void setUnits(List<UnitDto> units) {
        this.units = units;
    }

    public List<StringCategoryDto> getStringCategories() {
        return stringCategories;
    }

    public void setStringCategories(List<StringCategoryDto> stringCategories) {
        this.stringCategories = stringCategories;
    }
}
