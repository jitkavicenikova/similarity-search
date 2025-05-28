package thesis.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash("stringCategory")
public class StringCategory implements Identifiable {
    @Id
    @NotNull(message = "name cannot be null")
    @Size(min = 1, max = 20, message = "name must be between 1 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "name can only contain letters, numbers, '_', and '-'")
    private String name;
    @NotNull(message = "isComparable cannot be null")
    private Boolean isComparable;
    @NotEmpty(message = "values cannot be empty")
    private List<String> values;

    public StringCategory(String name, Boolean isComparable, List<String> values) {
        this.name = name;
        this.isComparable = isComparable;
        this.values = values;
    }

    public StringCategory() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsComparable() {
        return isComparable;
    }

    public void setIsComparable(Boolean isComparable) {
        this.isComparable = isComparable;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @JsonIgnore
    @Override
    public String getId() {
        return name;
    }
}
