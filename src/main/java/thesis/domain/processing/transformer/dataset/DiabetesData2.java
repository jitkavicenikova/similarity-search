package thesis.domain.processing.transformer.dataset;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import thesis.domain.processing.dto.DataSetDto;
import thesis.domain.processing.dto.MarkerDto;
import thesis.domain.processing.dto.RecordDto;
import thesis.domain.processing.dto.ResultDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DiabetesData2 extends DiabetesDataBase {
    public DataSetDto readDataFromFile(BufferedReader reader) throws IOException {
        var dataSet = createCommonElements();

        String line;

        var records = new ArrayList<RecordDto>();
        var results = new ArrayList<ResultDto>();
        int index = 0;
        while ((line = reader.readLine()) != null) {
            String[] split = line.split(",");

            records.add(parseRecord(index, split));
            results.addAll(parseDiabetesResults(dataSet.getMarkers(), index, split));

            index++;
        }

        dataSet.setRecords(records);
        dataSet.setResults(results);

        return dataSet;
    }

    private RecordDto parseRecord(int index, String[] split) {
        var record = new RecordDto(index);

        JSONObject metadata = new JSONObject();
        metadata.put("location", split[6]);

        record.setMetadata(metadata.toString());
        return record;
    }

    private List<ResultDto> parseDiabetesResults(List<MarkerDto> markers, int recordId, String[] split) {
        Map<String, Integer> columnIndex = Map.ofEntries(
                Map.entry("chol", 1),
                Map.entry("stab.glu", 2),
                Map.entry("hdl", 3),
                Map.entry("ratio", 4),
                Map.entry("glyhb", 5),
                Map.entry("age", 7),
                Map.entry("gender", 8),
                Map.entry("height", 9),
                Map.entry("weight", 10),
                Map.entry("frame", 11),
                Map.entry("bp.1s", 12),
                Map.entry("bp.1d", 13),
                Map.entry("bp.2s", 14),
                Map.entry("bp.2d", 15),
                Map.entry("waist", 16),
                Map.entry("hip", 17),
                Map.entry("time.ppn", 18)
        );

        return parseResultsByColumnMapping(markers, recordId, split, columnIndex);
    }

    @Override
    protected ResultDto getResultDto(int recordId, MarkerDto marker, String rawValue) {
        ResultDto result;
        if (marker.getName().equals(MARKER_GENDER)) {
            result = new ResultDto(recordId, marker.getName(), rawValue, MARKER_GENDER, null, null);
        } else if (marker.getName().equals(MARKER_FRAME)) {
            result = new ResultDto(recordId, marker.getName(), rawValue, MARKER_FRAME, null, null);
        } else {
            double value = Double.parseDouble(rawValue);
            if (value == 0) {
                return null;
            }
            result = new ResultDto(recordId, marker.getName(), value, value,
                    null, null, null, null, null);

            if (marker.getName().equals(MARKER_GLUCOSE)) {
                result.setTechnologyName(TECH_GLUC_UNKNOWN);
            }
        }

        return result;
    }
}
