package thesis.domain.search.dto;

import java.time.LocalDateTime;

public class SearchFilters {
    private String technologyName;
    private Boolean includeComparableTechnologies;
    private String sample;
    private LocalDateTime fromTimestamp;
    private LocalDateTime toTimestamp;

    public SearchFilters() {
    }

    public SearchFilters(String technologyName, Boolean includeComparableTechnologies, String sample,
                         LocalDateTime fromTimestamp, LocalDateTime toTimestamp) {
        this.technologyName = technologyName;
        this.includeComparableTechnologies = includeComparableTechnologies;
        this.sample = sample;
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
    }

    public String getTechnologyName() {
        return technologyName;
    }

    public void setTechnologyName(String technologyName) {
        this.technologyName = technologyName;
    }

    public Boolean getIncludeComparableTechnologies() {
        return includeComparableTechnologies;
    }

    public void setIncludeComparableTechnologies(Boolean includeComparableTechnologies) {
        this.includeComparableTechnologies = includeComparableTechnologies;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public LocalDateTime getFromTimestamp() {
        return fromTimestamp;
    }

    public void setFromTimestamp(LocalDateTime fromTimestamp) {
        this.fromTimestamp = fromTimestamp;
    }

    public LocalDateTime getToTimestamp() {
        return toTimestamp;
    }

    public void setToTimestamp(LocalDateTime toTimestamp) {
        this.toTimestamp = toTimestamp;
    }
}
