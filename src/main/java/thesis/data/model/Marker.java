package thesis.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import thesis.data.enums.AggregationType;

import java.util.List;

@RedisHash("marker")
public class Marker implements Identifiable {
    @Id
    @NotNull(message = "name cannot be null")
    @Size(min = 1, max = 20, message = "name must be between 1 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "name can only contain letters, numbers, '_', and '-'")
    private String name;
    @Size(max = 1000, message = "description must be at most 1000 characters long")
    private String description;
    @Size(max = 100, message = "nameRaw must be at most 100 characters long")
    private String nameRaw;
    @Indexed
    private String unitName;

    private List<String> childMarkerNames;
    private AggregationType aggregationType;

    public Marker(String name, String description, String nameRaw, String unitName) {
        this.name = name;
        this.description = description;
        this.nameRaw = nameRaw;
        this.unitName = unitName;
    }

    public Marker(String name, String description, String nameRaw, String unitName,
                  List<String> childMarkerNames, AggregationType aggregationType) {
        this.name = name;
        this.description = description;
        this.nameRaw = nameRaw;
        this.unitName = unitName;
        this.childMarkerNames = childMarkerNames;
        this.aggregationType = aggregationType;
    }

    public Marker() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNameRaw() {
        return nameRaw;
    }

    public void setNameRaw(String nameRaw) {
        this.nameRaw = nameRaw;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<String> getChildMarkerNames() {
        return childMarkerNames;
    }

    public void setChildMarkerNames(List<String> childMarkerNames) {
        this.childMarkerNames = childMarkerNames;
    }

    public AggregationType getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(AggregationType aggregationType) {
        this.aggregationType = aggregationType;
    }

    @JsonIgnore
    @Override
    public String getId() {
        return name;
    }
}
