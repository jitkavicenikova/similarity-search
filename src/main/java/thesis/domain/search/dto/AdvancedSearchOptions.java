package thesis.domain.search.dto;

import java.util.List;

public class AdvancedSearchOptions {
    private List<NumericSearchOptions> numericOptions;
    private List<StringSearchOptions> stringOptions;
    private List<BoolSearchOptions> boolOptions;

    public List<NumericSearchOptions> getNumericOptions() {
        return numericOptions;
    }

    public void setNumericOptions(List<NumericSearchOptions> numericOptions) {
        this.numericOptions = numericOptions;
    }

    public List<StringSearchOptions> getStringOptions() {
        return stringOptions;
    }

    public void setStringOptions(List<StringSearchOptions> stringOptions) {
        this.stringOptions = stringOptions;
    }

    public List<BoolSearchOptions> getBoolOptions() {
        return boolOptions;
    }

    public void setBoolOptions(List<BoolSearchOptions> boolOptions) {
        this.boolOptions = boolOptions;
    }
}
