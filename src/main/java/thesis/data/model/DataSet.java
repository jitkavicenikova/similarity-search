package thesis.data.model;

import java.time.LocalDateTime;
import java.util.List;

public class DataSet {
    private String fileName;
    private LocalDateTime importDate;
    private List<Marker> markers;
    private List<Record> records;
    private List<Result> results;
    private List<Technology> technologies;
    private List<Unit> units;
    private List<StringCategory> stringCategories;

    public DataSet() {
    }

    public DataSet(List<Marker> markers, List<Record> records, List<Result> results,
                   List<Unit> units, List<Technology> technologies) {
        this.markers = markers;
        this.records = records;
        this.results = results;
        this.technologies = technologies;
        this.units = units;
    }

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

    public List<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<Marker> markers) {
        this.markers = markers;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public List<Technology> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<Technology> technologies) {
        this.technologies = technologies;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public List<StringCategory> getStringCategories() {
        return stringCategories;
    }

    public void setStringCategories(List<StringCategory> stringCategories) {
        this.stringCategories = stringCategories;
    }
}
