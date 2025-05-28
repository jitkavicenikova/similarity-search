package thesis.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash("technology")
public class Technology implements Identifiable {
    @Id
    @NotNull(message = "name cannot be null")
    @Size(min = 1, max = 20, message = "name must be between 1 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "name can only contain letters, numbers, '_', and '-'")
    private String name;
    private List<TechnologyProperties> properties;

    public Technology(String name) {
        this.name = name;
    }

    public Technology() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TechnologyProperties> getProperties() {
        return properties;
    }

    public void setProperties(List<TechnologyProperties> technologyProperties) {
        this.properties = technologyProperties;
    }

    @JsonIgnore
    @Override
    public String getId() {
        return name;
    }
}
