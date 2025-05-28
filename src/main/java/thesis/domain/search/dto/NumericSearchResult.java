package thesis.domain.search.dto;

import thesis.data.model.Result;

import java.util.List;

public class NumericSearchResult {
    private List<Result> results;
    private List<AggregatedResult> aggregatedResults;

    public NumericSearchResult(List<Result> results, List<AggregatedResult> aggregatedResults) {
        this.results = results;
        this.aggregatedResults = aggregatedResults;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public List<AggregatedResult> getAggregatedResults() {
        return aggregatedResults;
    }

    public void setAggregatedResults(List<AggregatedResult> aggregatedResults) {
        this.aggregatedResults = aggregatedResults;
    }
}
