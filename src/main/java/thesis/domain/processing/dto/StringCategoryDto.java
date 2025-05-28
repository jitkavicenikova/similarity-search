package thesis.domain.processing.dto;

import java.util.List;

public class StringCategoryDto {
    private String name;
    private Boolean isComparable;
    private List<String> values;

    public StringCategoryDto() {
    }

    public StringCategoryDto(String name, Boolean isComparable, List<String> values) {
        this.name = name;
        this.isComparable = isComparable;
        this.values = values;
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
}
