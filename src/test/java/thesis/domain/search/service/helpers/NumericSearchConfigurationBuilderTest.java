package thesis.domain.search.service.helpers;

import org.junit.jupiter.api.Test;
import thesis.domain.search.dto.NumericSearchConfiguration;
import thesis.domain.search.dto.NumericSearchOptions;

import static org.junit.jupiter.api.Assertions.*;

class NumericSearchConfigurationBuilderTest {
    private final NumericSearchConfigurationBuilder builder = new NumericSearchConfigurationBuilder();

    @Test
    void getSearchConfiguration_ShouldApplyAbsoluteDeviation_WhenValueIsSet() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setValue(100.0);
        options.setAbsoluteDeviation(15.0);

        NumericSearchConfiguration config = builder.getSearchConfiguration(options);

        assertEquals(85.0, config.getMinimum());  // 100 - 15
        assertEquals(115.0, config.getMaximum()); // 100 + 15
    }

    @Test
    void getSearchConfiguration_ShouldApplyPercentageDeviation_WhenMinimumIsSet() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(200.0);
        options.setPercentageDeviation(10.0);

        NumericSearchConfiguration config = builder.getSearchConfiguration(options);

        assertEquals(180.0, config.getMinimum()); // 200 - 10%
        assertEquals(Double.POSITIVE_INFINITY, config.getMaximum()); // No maximum set
    }

    @Test
    void getSearchConfiguration_ShouldApplyAbsoluteTolerance_WhenMaximumIsSet() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMaximum(300.0);
        options.setAbsoluteTolerance(20.0);

        NumericSearchConfiguration config = builder.getSearchConfiguration(options);

        assertEquals(300, config.getMaximum());
        assertEquals(320.0, config.getMaximumWithTolerance()); // 300 + 20 (apply tolerance)
    }

    @Test
    void getSearchConfiguration_ShouldApplyPercentageTolerance_WhenBothMinAndMaxAreSet() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(50.0);
        options.setMaximum(150.0);
        options.setPercentageTolerance(5.0); // 5% tolerance

        NumericSearchConfiguration config = builder.getSearchConfiguration(options);

        assertEquals(50.0, config.getMinimum());
        assertEquals(150.0, config.getMaximum());
        assertEquals(47.5, config.getMinimumWithTolerance()); // 50 - 5%
        assertEquals(157.5, config.getMaximumWithTolerance()); // 150 + 5%
    }

    @Test
    void getSearchConfiguration_ShouldApplyAbsoluteDeviationAndTolerance_WhenValueIsSet() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setValue(100.0);
        options.setAbsoluteDeviation(10.0); // Apply deviation
        options.setAbsoluteTolerance(5.0);  // Apply tolerance

        NumericSearchConfiguration config = builder.getSearchConfiguration(options);

        assertEquals(90.0, config.getMinimum());  // 100 - 10
        assertEquals(110.0, config.getMaximum()); // 100 + 10
        assertEquals(85.0, config.getMinimumWithTolerance()); // 90 - 5
        assertEquals(115.0, config.getMaximumWithTolerance()); // 110 + 5
    }

    @Test
    void getSearchConfiguration_ShouldApplyPercentageDeviationAndTolerance_WhenMinimumIsSet() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(80.0);
        options.setPercentageDeviation(5.0);  // 5% deviation
        options.setPercentageTolerance(10.0); // 10% tolerance

        NumericSearchConfiguration config = builder.getSearchConfiguration(options);

        // Apply deviation first: 80 -> 76
        // Then tolerance: 76 -> 68.4 (10% reduction)
        assertEquals(76.0, config.getMinimum());
        assertEquals(68.4, config.getMinimumWithTolerance());
    }

    @Test
    void getSearchConfiguration_ShouldApplyCombination_WhenMaximumIsSet() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMaximum(60.0);
        options.setAbsoluteDeviation(5.0);     // Absolute deviation
        options.setPercentageTolerance(20.0);  // 20% percentage tolerance

        NumericSearchConfiguration config = builder.getSearchConfiguration(options);

        assertEquals(65.0, config.getMaximum()); // 60 + 5 (deviation)
        assertEquals(78.0, config.getMaximumWithTolerance()); // 65 + 20% tolerance
    }

    @Test
    void getSearchConfiguration_ShouldSetDefaultValues_WhenNoDeviationOrToleranceIsSet() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setMinimum(30.0);
        options.setMaximum(70.0);

        NumericSearchConfiguration config = builder.getSearchConfiguration(options);

        assertEquals(30.0, config.getMinimum());
        assertEquals(70.0, config.getMaximum());
        assertEquals(30.0, config.getMinimumWithTolerance()); // No tolerance, minWithTolerance = min
        assertEquals(70.0, config.getMaximumWithTolerance()); // No tolerance, maxWithTolerance = max
    }


    @Test
    void getSearchConfiguration_ShouldSetWithToleranceFlag_WhenToleranceIsNonZero() {
        NumericSearchOptions options = new NumericSearchOptions();
        options.setAbsoluteTolerance(1.0);

        NumericSearchConfiguration config = builder.getSearchConfiguration(options);

        assertTrue(config.getWithTolerance());
    }

    @Test
    void getSearchConfiguration_ShouldSetUseTechnologyDeviationToFalse_WhenNotProvided() {
        NumericSearchOptions options = new NumericSearchOptions();

        NumericSearchConfiguration config = builder.getSearchConfiguration(options);

        assertFalse(config.getUseTechnologyDeviation());
    }
}
