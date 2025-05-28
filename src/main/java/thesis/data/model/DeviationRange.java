package thesis.data.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class DeviationRange {
    @NotNull(message = "from cannot be null")
    private Double from;
    @NotNull(message = "to cannot be null")
    private Double to;
    @NotNull(message = "deviation cannot be null")
    @PositiveOrZero(message = "deviation must be 0 or larger")
    private Double deviation;

    public DeviationRange(Double from, Double to, Double deviation) {
        this.from = from;
        this.to = to;
        this.deviation = deviation;
    }

    public DeviationRange() {
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
