package thesis.domain.processing.transformer.dataset;

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
public class DiabetesData1 extends DiabetesDataBase {
    public DataSetDto readDataFromFile(BufferedReader reader) throws IOException {
        var dataSet = createCommonElements();

        String line;

        var records = new ArrayList<RecordDto>();
        var results = new ArrayList<ResultDto>();
        int index = 0;
        while ((line = reader.readLine()) != null) {
            String[] split = line.split(",", -1);
            records.add(new RecordDto(index));
            results.addAll(parseDiabetesResults(dataSet.getMarkers(), index, split));

            index++;
        }

        dataSet.setRecords(records);
        dataSet.setResults(results);

        return dataSet;
    }

    private List<ResultDto> parseDiabetesResults(List<MarkerDto> markers, int recordId, String[] split) {
        Map<String, Integer> columnIndex = Map.of(
                "Pregnancies", 0,
                "Glucose", 1,
                "BloodPressure", 2,
                "SkinThickness", 3,
                "Insulin", 4,
                "BMI", 5,
                "DiabetesPedigreeFunction", 6,
                "Age", 7,
                "Outcome", 8
        );

        return parseResultsByColumnMapping(markers, recordId, split, columnIndex);
    }

    @Override
    protected ResultDto getResultDto(int recordId, MarkerDto marker, String rawValue) {
        ResultDto result;
        if (marker.getName().equals(MARKER_OUTCOME)) {
            Boolean isDiabetic = rawValue.equals("1");
            result = new ResultDto(recordId, marker.getName(), isDiabetic, null, null);
        } else {
            double value = Double.parseDouble(rawValue);
            if (value == 0) {
                return null;
            }
            result = new ResultDto(recordId, marker.getName(), value, value,
                    null, null, null, null, null);

            if (marker.getName().equals(MARKER_GLUCOSE)) {
                result.setTechnologyName(TECH_GLUC_PLASMA);
            }
        }

        return result;
    }
}
