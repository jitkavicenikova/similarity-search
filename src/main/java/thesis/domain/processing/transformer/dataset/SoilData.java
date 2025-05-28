package thesis.domain.processing.transformer.dataset;

import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import thesis.domain.processing.dto.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Profile("soil")
public class SoilData implements DataTransformer {
    private final static String UNIT_DD = "DD";
    private final static String UNIT_CM = "cm";
    private final static String UNIT_MGKG = "mg/kg";
    private final static String UNIT_GKG = "g/kg";
    private final static String UNIT_DSM = "dS/m";

    private final static String MARKER_ALUMINIUM = "aluminium";
    private final static String MARKER_BORON = "boron";
    private final static String MARKER_POTASSIUM = "potassium";
    private final static String MARKER_CALCIUM = "calcium";
    private final static String MARKER_MAGNESIUM = "magnesium";
    private final static String MARKER_MANGANESE = "manganese";
    private final static String MARKER_ZINC = "zinc";
    private final static String MARKER_IRON = "iron";
    private final static String MARKER_COPPER = "copper";
    private final static String MARKER_SULPHUR = "sulphur";
    private final static String MARKER_PHOSPHORUS = "phosphorus";
    private final static String MARKER_SODIUM = "sodium";

    private final static String MARKER_CARBON_ORGANIC = "carbon_organic";
    private final static String MARKER_CARBON_TOTAL = "carbon_total";
    private final static String MARKER_NITROGEN_TOTAL = "nitrogen_total";

    private final static String TECH_MEHLICH = "Mehlich3";
    private final static String TECH_COMBUSTION = "combustion";

    private Set<String> mehlichElements;
    private Set<String> combustionElements;

    @Override
    public DataSetDto transformFull(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return readDataFromFile(reader);
        }
    }

    @Override
    public DataSetDto transformIncrement(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            var dataSet = readDataFromFile(reader);
            dataSet.setUnits(null);
            dataSet.setTechnologies(null);
            dataSet.setMarkers(null);
            return dataSet;
        }
    }

    private DataSetDto readDataFromFile(BufferedReader reader) throws IOException {
        var dataSet = new DataSetDto();
        var units = createUnits();
        var markers = createMarkers();

        dataSet.setUnits(units);
        dataSet.setMarkers(markers);
        dataSet.setTechnologies(createTechnologies());

        String line;
        reader.readLine(); // skip the header

        var records = new ArrayList<RecordDto>();
        var results = new ArrayList<ResultDto>();
        int index = 0;
        while ((line = reader.readLine()) != null) {
            String[] split = line.split("\\|", -1);

            records.add(parseRecord(index, split));
            results.addAll(parseResults(markers, index, split));

            index++;
        }

        dataSet.setRecords(records);
        dataSet.setResults(results);

        return dataSet;
    }

    private RecordDto parseRecord(int index, String[] split) {
        var record = new RecordDto(index);

        JSONObject metadata = new JSONObject();
        metadata.put("startDate", split[2]);
        metadata.put("endDate", split[3]);
        metadata.put("source", split[4]);

        record.setMetadata(metadata.toString());
        return record;
    }

    private List<ResultDto> parseResults(List<MarkerDto> markers, int recordId, String[] split) {
        List<ResultDto> results = new ArrayList<>();

        // longitude & latitude
        var longitude = Double.parseDouble(split[0]);
        results.add(new ResultDto(recordId, markers.get(0).getName(), longitude, longitude, null, null, null, null, null));
        var latitude = Double.parseDouble(split[1]);
        results.add(new ResultDto(recordId, markers.get(1).getName(), latitude, latitude, null, null, null, null, null));

        for (int markerId = 0; markerId <= 18; markerId++) {
            var result = createResult(recordId, markers.get(markerId + 2).getName(), split[markerId + 5]);
            if (result != null) {
                results.add(result);
            }
        }

        return results;
    }

    private ResultDto createResult(int recordId, String markerName, String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        var result = new ResultDto();
        result.setRecordId(recordId);
        result.setMarkerName(markerName);

        var value = Double.parseDouble(rawValue);
        result.setMin(value);
        result.setMax(value);

        if (mehlichElements.contains(markerName)) {
            result.setTechnologyName(TECH_MEHLICH);
        } else if (combustionElements.contains(markerName)) {
            result.setTechnologyName(TECH_COMBUSTION);
        }

        return result;
    }

    private List<MarkerDto> createMarkers() {
        return List.of(
                new MarkerDto("longitude", "Longitude of soil sampling location", "longitude", UNIT_DD),
                new MarkerDto("latitude", "Latitude of soil sampling location", "latitude", UNIT_DD),
                new MarkerDto("horizon_low", "Depth of bottom of sampled layer", "horizon_lower", UNIT_CM),
                new MarkerDto("horizon_up", "Depth of top of sampled layer", "horizon_upper", UNIT_CM),
                new MarkerDto(MARKER_ALUMINIUM, "Aluminium", "aluminium_extractable", UNIT_MGKG),
                new MarkerDto(MARKER_BORON, "Boron", "boron_extractable", UNIT_MGKG),
                new MarkerDto(MARKER_CALCIUM, "Calcium", "calcium_extractable", UNIT_MGKG),
                new MarkerDto(MARKER_CARBON_ORGANIC, "Organic Carbon", "carbon_organic", UNIT_GKG),
                new MarkerDto(MARKER_CARBON_TOTAL, "Total Carbon", "carbon_total", UNIT_GKG),
                new MarkerDto(MARKER_COPPER, "Copper", "copper_extractable", UNIT_MGKG),
                new MarkerDto("el_conductivity", "Electrical conductivity, Saturation Extract", "electrical_conductivity", UNIT_DSM),
                new MarkerDto(MARKER_IRON, "Iron", "iron_extractable", UNIT_MGKG),
                new MarkerDto(MARKER_MAGNESIUM, "Magnesium", "magnesium_extractable", UNIT_MGKG),
                new MarkerDto(MARKER_MANGANESE, "Manganese", "manganese_extractable", UNIT_MGKG),
                new MarkerDto(MARKER_NITROGEN_TOTAL, "Total Nitrogen", "nitrogen_total", UNIT_GKG),
                new MarkerDto("ph", "pH, measured in 1:1 soil-water suspension", "ph", null),
                new MarkerDto(MARKER_PHOSPHORUS, "Phosphorus", "phosphorus_extractable", UNIT_MGKG),
                new MarkerDto(MARKER_POTASSIUM, "Potassium", "potassium_etractable", UNIT_MGKG),
                new MarkerDto(MARKER_SODIUM, "Sodium", "sodium_extractable", UNIT_MGKG),
                new MarkerDto(MARKER_SULPHUR, "Sulphur", "sulphur_extractable", UNIT_MGKG),
                new MarkerDto(MARKER_ZINC, "Zinc", "zn_mehlich3", UNIT_MGKG)
        );
    }

    private List<UnitDto> createUnits() {
        var unit0 = new UnitDto(UNIT_DD, "Decimal degrees");
        var unit1 = new UnitDto(UNIT_CM, "Centimeters");
        var unit2 = new UnitDto(UNIT_MGKG, "mg/kg");
        var unit3 = new UnitDto(UNIT_GKG, "g/kg");
        var unit4 = new UnitDto(UNIT_DSM, "deciSiemens per meter");

        return List.of(unit0, unit1, unit2, unit3, unit4);
    }

    private List<TechnologyDto> createTechnologies() {
        var properties1 = new TechnologyPropertiesDto(MARKER_POTASSIUM, true, createDeviation(5.01), null);
        var properties2 = new TechnologyPropertiesDto(MARKER_CALCIUM, true, createDeviation(6.395), null);
        var properties3 = new TechnologyPropertiesDto(MARKER_MAGNESIUM, true, createDeviation(4.0), null);
        var properties4 = new TechnologyPropertiesDto(MARKER_MANGANESE, true, createDeviation(5.84), null);
        var properties5 = new TechnologyPropertiesDto(MARKER_ZINC, true, createDeviation(23.405), null);
        var properties6 = new TechnologyPropertiesDto(MARKER_IRON, true, createDeviation(5.03), null);
        var properties7 = new TechnologyPropertiesDto(MARKER_COPPER, true, createDeviation(7.425), null);

        mehlichElements = Set.of(MARKER_MANGANESE, MARKER_POTASSIUM, MARKER_ZINC, MARKER_IRON,
                MARKER_COPPER, MARKER_ALUMINIUM, MARKER_BORON, MARKER_CALCIUM, MARKER_MAGNESIUM,
                MARKER_SODIUM, MARKER_SULPHUR, MARKER_PHOSPHORUS);
        combustionElements = Set.of(MARKER_CARBON_ORGANIC, MARKER_CARBON_TOTAL, MARKER_NITROGEN_TOTAL);

        return List.of(new TechnologyDto(TECH_COMBUSTION, null),
                new TechnologyDto(TECH_MEHLICH, List.of(properties1, properties2, properties3, properties4, properties5, properties6, properties7)));
    }

    private List<DeviationRangeDto> createDeviation(Double deviation) {
        return List.of(new DeviationRangeDto(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, deviation));
    }
}
