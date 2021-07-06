package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomKPITest {

    public static final String LABEL = "test";
    private KPIConfig kpiConfig;

    @BeforeEach
    void setup() {
        kpiConfig = new KPIConfig();
        kpiConfig.label = LABEL;
    }

    @ParameterizedTest
    @CsvSource({
            "2.58, green",
            "0, green",
            "10.1, yellow",
    })
    void testWithRanges1(String value, String status) {
        CustomKPI test = new CustomKPI();
        kpiConfig.ranges = getRangeMap();
        test.init(kpiConfig);

        //when
        StatusValue statusValue = test.getStatusValues(getComponent(value)).get(0);

        //then
        assertNotNull(statusValue);
        Assertions.assertEquals(status, statusValue.getStatus().getName().toLowerCase(Locale.ROOT));
    }

    @Test
    void testUsesConfigValues() {
        CustomKPI test = new CustomKPI();
        kpiConfig.ranges = getRangeMap();
        kpiConfig.description = "bar";

        //when
        test.init(kpiConfig);

        //then
        assertThat(test.getDescription()).isEqualTo("bar");
    }

    @Test
    void testoutOfRange() {
        CustomKPI test = new CustomKPI();
        kpiConfig.ranges = getRangeMap();
        test.init(kpiConfig);
        List<StatusValue> statusValues = test.getStatusValues(getComponent("100.1"));
        Assertions.assertTrue(statusValues.isEmpty());
    }

    @Test
    void brokenRangeConfig() {
        CustomKPI test = new CustomKPI();
        kpiConfig.ranges = getRangeMap();
        kpiConfig.ranges.put(Status.GREEN.name(), "0-12");
        assertThrows(ProcessingException.class, () -> test.init(kpiConfig));
    }

    @Test
    void withoutMessageTemplate() {
        CustomKPI test = new CustomKPI();

        kpiConfig.ranges = getRangeMap();
        test.init(kpiConfig);

        //when
        StatusValue statusValue = test.getStatusValues(getComponent("10.1")).get(0);

        //then
        assertNotNull(statusValue);
        Assertions.assertEquals("10.1", statusValue.getMessage());
    }

    @Test
    void withMessageTemplate() {
        CustomKPI test = new CustomKPI();

        kpiConfig.ranges = getRangeMap();
        kpiConfig.messageTemplate = "foo bar: %s";
        test.init(kpiConfig);

        //when
        StatusValue statusValue = test.getStatusValues(getComponent("10.1")).get(0);

        //then
        assertNotNull(statusValue);
        Assertions.assertEquals("foo bar: 10.1", statusValue.getMessage());
    }

    @Test
    void brokenMatchesConfig() {
        Map<String, String> matches = getMatches();
        matches.put(Status.GREEN.name(), "0-12[");
        kpiConfig.matches = matches;
        CustomKPI customKPI = new CustomKPI();

        assertThrows(ProcessingException.class, () -> customKPI.init(kpiConfig));
    }

    @Test
    void RangeOneNumber() {
        Map<String, String> r2 = getRangeMap();
        r2.put(Status.GREEN.name(), "0");
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.ranges = r2;
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("0")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }


    @Test
    void testWithMatches1() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("OK")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    void testWithMatches2() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("good")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    void testWithMatches3() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("nice")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    void testWithMatches4() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("bad")).get(0);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    void testWithMatches5() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("error")).get(0);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    void noMatchIsEmpty() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        List<StatusValue> foo = customKPI.getStatusValues(getComponent("foo"));
        assertTrue(foo.isEmpty());
    }

    @Test
    void testWithRangesAndMatches1() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        kpiConfig.ranges = getRangeMap();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("error")).get(0);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    void testWithRangesAndMatches2() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        kpiConfig.ranges = getRangeMap();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("20.53")).get(0);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    private Item getComponent(String value) {
        Item item = getTestItem("test", "a");
        item.setLabel(LABEL, value);
        item.setLabel("asMessageLabel", value +" foo");
        return item;
    }

    private Map<String, String> getRangeMap() {
        Map<String, String> rangeMap = new HashMap<>();
        rangeMap.put(Status.GREEN.name(), "0;9.99999999");
        rangeMap.put(Status.YELLOW.name(), "10;15");
        rangeMap.put(Status.ORANGE.name(), "15;19.999999999");
        rangeMap.put(Status.RED.name(), "20;40");
        return rangeMap;
    }

    private Map<String, String> getMatches() {
        HashMap<String, String> map = new HashMap<>();

        map.put(Status.GREEN.name(), "OK;good;nice");
        map.put(Status.RED.name(), "BAD;err.*");

        return map;
    }

}