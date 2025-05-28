package thesis.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash("unit")
public class Unit implements Identifiable {
    @Id
    @NotNull(message = "name cannot be null")
    @Size(min = 1, max = 20, message = "name must be between 1 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9/_-]+$", message = "name can only contain letters, numbers, '/', '_', and '-'")
    private String name;
    @Size(max = 100, message = "nameRaw must be at most 100 characters long")
    private String nameRaw;
    private List<Conversion> conversions;

    public Unit(String name, String nameRaw) {
        this.name = name;
        this.nameRaw = nameRaw;
    }

    public Unit() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameRaw() {
        return nameRaw;
    }

    public void setNameRaw(String nameRaw) {
        this.nameRaw = nameRaw;
    }

    public List<Conversion> getConversions() {
        return conversions;
    }

    public void setConversions(List<Conversion> conversions) {
        this.conversions = conversions;
    }

    @JsonIgnore
    @Override
    public String getId() {
        return name;
    }
}
