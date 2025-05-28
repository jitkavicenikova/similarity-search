package thesis.domain.search.dto;

import jakarta.validation.constraints.NotNull;
import thesis.domain.search.dto.enums.StringSearchType;

import java.util.List;

public class StringSearchOptions {
    @NotNull(message = "Marker name must be provided")
    private String markerName;
    private String value;
    private List<String> values;
    private StringSearchType searchType;
    private String categoryName;

    private SearchFilters filters;

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StringSearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(StringSearchType searchType) {
        this.searchType = searchType;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public SearchFilters getFilters() {
        return filters;
    }

    public void setFilters(SearchFilters filters) {
        this.filters = filters;
    }
}
