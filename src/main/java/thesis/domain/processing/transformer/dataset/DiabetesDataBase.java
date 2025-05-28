package thesis.domain.processing.transformer.dataset;

import thesis.data.enums.AggregationType;
import thesis.domain.processing.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DiabetesDataBase {
    protected static final String UNIT_MMHG = "mmHg";
    protected static final String UNIT_MGDL = "mg/dL";
    protected static final String UNIT_MM = "mm";
    protected static final String UNIT_MUUML = "muU/ml";
    protected static final String UNIT_KGM2 = "kg/m2";
    protected static final String UNIT_IN = "in";
    protected static final String UNIT_LBS = "lbs";
    protected static final String UNIT_MIN = "min";
    protected static final String UNIT_CM = "cm";
    protected static final String UNIT_KG = "kg";

    protected static final String MARKER_SYSTOLIC_BP1 = "systolic_bp1";
    protected static final String MARKER_DIASTOLIC_BP1 = "diastolic_bp1";
    protected static final String MARKER_SYSTOLIC_BP2 = "systolic_bp2";
    protected static final String MARKER_DIASTOLIC_BP2 = "diastolic_bp2";
    protected static final String MARKER_AVG_SYSTOLIC_BP = "average_systolic_bp";
    protected static final String MARKER_AVG_DIASTOLIC_BP = "average_diastolic_bp";

    protected static final String MARKER_CHOL = "chol";
    protected static final String MARKER_GLUCOSE = "glucose";
    protected static final String MARKER_HDL = "hdl";
    protected static final String MARKER_RATIO = "ratio";
    protected static final String MARKER_HBA1C = "HbA1c";
    protected static final String MARKER_AGE = "age";
    protected static final String MARKER_GENDER = "gender";
    protected static final String MARKER_HEIGHT = "height";
    protected static final String MARKER_WEIGHT = "weight";
    protected static final String MARKER_FRAME = "frame";
    protected static final String MARKER_WAIST = "waist";
    protected static final String MARKER_HIP = "hip";
    protected static final String MARKER_TIME_PPN = "time_ppn";
    protected static final String MARKER_PREGNANCIES = "pregnancies";
    protected static final String MARKER_SKIN_THICKNESS = "skin_thickness";
    protected static final String MARKER_INSULIN = "insulin";
    protected static final String MARKER_BMI = "BMI";
    protected static final String MARKER_PED_FUNCTION = "ped_function";
    protected static final String MARKER_OUTCOME = "outcome";

    protected static final String TECH_GLUC_PLASMA = "plasma_glucose";
    protected static final String TECH_GLUC_UNKNOWN = "unknown_glucose";

    protected static final String CATEGORY_FRAME = "frame";
    protected static final String CATEGORY_GENDER = "gender";

    protected abstract ResultDto getResultDto(int recordId, MarkerDto marker, String rawValue);

    protected DataSetDto createCommonElements() {
        var dataset = new DataSetDto();
        dataset.setUnits(createUnits());
        dataset.setTechnologies(createTechnologies());
        dataset.setMarkers(createMarkers());
        dataset.setStringCategories(createCategories());

        return dataset;
    }

    protected List<ResultDto> parseResultsByColumnMapping(List<MarkerDto> markers, int recordId,
                                                          String[] split, Map<String, Integer> columnIndexMap) {
        List<ResultDto> results = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : columnIndexMap.entrySet()) {
            String columnName = entry.getKey();
            int index = entry.getValue();

            if (index >= split.length) continue;
            String rawValue = split[index];
            if (rawValue == null || rawValue.isBlank()) continue;

            MarkerDto marker = markers.stream()
                    .filter(m -> m.getNameRaw() != null &&
                            m.getNameRaw().toLowerCase().contains(columnName.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            if (marker == null) continue;
            var result = getResultDto(recordId, marker, rawValue);
            if (result == null) continue;
            results.add(result);
        }

        return results;
    }

    private List<UnitDto> createUnits() {
        var unit0 = new UnitDto(UNIT_MMHG, "mm Hg");
        var unit1 = new UnitDto(UNIT_MGDL, "mg/dL");
        var unit2 = new UnitDto(UNIT_MM, "mm");
        var unit3 = new UnitDto(UNIT_MUUML, "mu U/ml");
        var unit4 = new UnitDto(UNIT_KGM2, "kg/m²");
        var unit5 = new UnitDto(UNIT_LBS, "pounds");
        var unit6 = new UnitDto(UNIT_IN, "inches");
        var unit7 = new UnitDto(UNIT_MIN, "minutes");
        var unit8 = new UnitDto(UNIT_CM, "cm");
        var unit9 = new UnitDto(UNIT_KG, "kg");
        unit8.setConversions(List.of(
                new ConversionDto(UNIT_IN, MARKER_HEIGHT, "x/2.54"),
                new ConversionDto(UNIT_IN, MARKER_WAIST, "x/2.54"),
                new ConversionDto(UNIT_IN, MARKER_HIP, "x/2.54")
        ));
        unit6.setConversions(List.of(
                new ConversionDto(UNIT_CM, MARKER_HEIGHT, "x*2.54"),
                new ConversionDto(UNIT_CM, MARKER_WAIST, "x*2.54"),
                new ConversionDto(UNIT_CM, MARKER_HIP, "x*2.54")
        ));
        unit9.setConversions(List.of(
                new ConversionDto(UNIT_LBS, MARKER_WEIGHT, "x*2.20462262")
        ));

        unit5.setConversions(List.of(
                new ConversionDto(UNIT_KG, MARKER_WEIGHT, "x/2.20462262")
        ));

        return List.of(unit0, unit1, unit2, unit3, unit4, unit5, unit6, unit7, unit8, unit9);
    }

    private List<TechnologyDto> createTechnologies() {
        return List.of(new TechnologyDto(TECH_GLUC_PLASMA, null),
                new TechnologyDto(TECH_GLUC_UNKNOWN, null));
    }

    private List<MarkerDto> createMarkers() {
        var systolicBp1 = new MarkerDto(MARKER_SYSTOLIC_BP1, "Systolic blood pressure first measurement.", "bp.1s", UNIT_MMHG);
        var diastolicBp1 = new MarkerDto(MARKER_DIASTOLIC_BP1, "Diastolic blood pressure first measurement.", "bp.1d, BloodPressure", UNIT_MMHG);
        var systolicBp2 = new MarkerDto(MARKER_SYSTOLIC_BP2, "Systolic blood pressure second measurement.", "bp.2s", UNIT_MMHG);
        var diastolicBp2 = new MarkerDto(MARKER_DIASTOLIC_BP2, "Diastolic blood pressure second measurement.", "bp.2d", UNIT_MMHG);

        var averageSysBp = new MarkerDto(MARKER_AVG_SYSTOLIC_BP, "Average systolic blood pressure.", "avg.bp.s", UNIT_MMHG);
        averageSysBp.setChildMarkerNames(List.of(systolicBp1.getName(), systolicBp2.getName()));
        averageSysBp.setAggregationType(AggregationType.AVERAGE);

        var averageDiaBp = new MarkerDto(MARKER_AVG_DIASTOLIC_BP, "Average diastolic blood pressure.", "avg.bp.d", UNIT_MMHG);
        averageDiaBp.setChildMarkerNames(List.of(diastolicBp1.getName(), diastolicBp2.getName()));
        averageDiaBp.setAggregationType(AggregationType.AVERAGE);

        return List.of(
                new MarkerDto(MARKER_CHOL, "Cholesterol.", "chol", UNIT_MGDL),
                new MarkerDto(MARKER_GLUCOSE, "Glucose concentration", "Glucose, stab.gluc", UNIT_MGDL),
                new MarkerDto(MARKER_HDL, "High-density lipoprotein.", "hdl", UNIT_MGDL),
                new MarkerDto(MARKER_RATIO, "Cholesterol/HDL ratio.", "ratio", null),
                new MarkerDto(MARKER_HBA1C, "Glycosolated hemoglobin (HbA1c).", "glyhb", null),
                new MarkerDto(MARKER_AGE, "Age of the patient.", "age", null),
                new MarkerDto(MARKER_GENDER, "Gender of the patient.", "gender", null),
                new MarkerDto(MARKER_HEIGHT, "Height of the patient.", "height", UNIT_IN),
                new MarkerDto(MARKER_WEIGHT, "Weight of the patient.", "weight", UNIT_LBS),
                new MarkerDto(MARKER_FRAME, "Patients frame.", "frame", null),
                systolicBp1,
                diastolicBp1,
                systolicBp2,
                diastolicBp2,
                new MarkerDto(MARKER_WAIST, "Waist circumference.", "waist", UNIT_IN),
                new MarkerDto(MARKER_HIP, "Hip circumference.", "hip", UNIT_IN),
                new MarkerDto(MARKER_TIME_PPN, "Time Postprandial.", "time.ppn", UNIT_MIN),
                new MarkerDto(MARKER_PREGNANCIES, "Number of pregnancies", "Pregnancies", null),
                new MarkerDto(MARKER_SKIN_THICKNESS, "Triceps skinfold thickness.", "SkinThickness", UNIT_MM),
                new MarkerDto(MARKER_INSULIN, "2-hour serum insulin.", "Insulin", UNIT_MUUML),
                new MarkerDto(MARKER_BMI, "Body mass index.", "BMI", UNIT_KGM2),
                new MarkerDto(MARKER_PED_FUNCTION, "Genetic predisposition score based on family history.", "DiabetesPedigreeFunction", null),
                new MarkerDto(MARKER_OUTCOME, "Diabetes status (1 = diabetes, 0 = no diabetes).", "Outcome", null),
                averageSysBp,
                averageDiaBp
        );
    }

    private List<StringCategoryDto> createCategories() {
        var category1 = new StringCategoryDto(CATEGORY_FRAME, true, List.of("small", "medium", "large"));
        var category2 = new StringCategoryDto(CATEGORY_GENDER, false, List.of("female", "male"));
        return List.of(category1, category2);
    }
}
