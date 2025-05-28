package thesis.domain.processing.dto;

/**
 * Represents a deviation range for a marker.
 */
public class DeviationRangeDto {
    private Double from;
    private Double to;
    private Double deviation;

    public DeviationRangeDto() {
    }

    public DeviationRangeDto(Double from, Double to, Double deviation) {
        this.from = from;
        this.to = to;
        this.deviation = deviation;
    }

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public Double getTo() {
        return to;
    }

    public void setTo(Double to) {
        this.to = to;
    }

    public Double getDeviation() {
        return deviation;
    }

    public void setDeviation(Double deviation) {
        this.deviation = deviation;
    }
}
