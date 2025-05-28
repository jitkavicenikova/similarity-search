package thesis.domain.processing.transformer.dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import thesis.domain.processing.dto.DataSetDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@Profile("diabetes")
public class DiabetesData implements DataTransformer {
    private final DiabetesData1 dataBase1;
    private final DiabetesData2 dataBase2;

    @Autowired
    public DiabetesData(DiabetesData1 diabetesData1, DiabetesData2 diabetesData2) {
        this.dataBase1 = diabetesData1;
        this.dataBase2 = diabetesData2;
    }

    @Override
    public DataSetDto transformFull(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            var line = reader.readLine();
            if (line.startsWith("Pregnancies")) {
                return dataBase1.readDataFromFile(reader);
            } else if (line.startsWith("id")) {
                return dataBase2.readDataFromFile(reader);
            } else {
                throw new IllegalArgumentException("Unsupported file format");
            }
        }
    }

    @Override
    public DataSetDto transformIncrement(MultipartFile file) throws IOException {
        var dataSet = transformFull(file);
        dataSet.setUnits(null);
        dataSet.setStringCategories(null);
        dataSet.setStringCategories(null);
        dataSet.setMarkers(null);

        return dataSet;
    }
}
