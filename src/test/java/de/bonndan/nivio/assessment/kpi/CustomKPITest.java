package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.*;

class CustomKPITest {

    public static final String LABEL = "test";
    private KPIConfig kpiConfig;

    @BeforeEach
    public void setup() {
        kpiConfig = new KPIConfig();
        kpiConfig.label = LABEL;
    }

    @Test
    public void testWithRanges1() {
        CustomKPI test = new CustomKPI();
        kpiConfig.ranges = getRangeMap();
        test.init(kpiConfig);
        StatusValue statusValue = test.getStatusValues(getComponent("2.58")).get(0);
        assertNotNull(statusValue);
        Assertions.assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithRanges2() {
        CustomKPI test = new CustomKPI();
        kpiConfig.ranges = getRangeMap();
        test.init(kpiConfig);
        StatusValue statusValue = test.getStatusValues(getComponent("0")).get(0);
        assertNotNull(statusValue);
        Assertions.assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithRanges3() {
        CustomKPI test = new CustomKPI();
        kpiConfig.ranges = getRangeMap();
        test.init(kpiConfig);
        StatusValue statusValue = test.getStatusValues(getComponent("10.1")).get(0);
        assertNotNull(statusValue);
        Assertions.assertEquals(Status.YELLOW, statusValue.getStatus());
    }

    @Test
    public void testoutOfRange() {
        CustomKPI test = new CustomKPI();
        kpiConfig.ranges = getRangeMap();
        test.init(kpiConfig);
        List<StatusValue> statusValues = test.getStatusValues(getComponent("100.1"));
        Assertions.assertTrue(statusValues.isEmpty());
    }

    @Test
    public void brokenRangeConfig() {
        CustomKPI test = new CustomKPI();
        kpiConfig.ranges = getRangeMap();
        kpiConfig.ranges.put(Status.GREEN.name(), "0-12");
        assertThrows(ProcessingException.class, () -> test.init(kpiConfig));
    }

    @Test
    public void brokenMatchesConfig() {
        Map<String, String> matches = getMatches();
        matches.put(Status.GREEN.name(), "0-12[");
        kpiConfig.matches = matches;
        CustomKPI customKPI = new CustomKPI();

        assertThrows(ProcessingException.class, () -> customKPI.init(kpiConfig));
    }

    @Test
    public void RangeOneNumber() {
        Map<String, String> r2 = getRangeMap();
        r2.put(Status.GREEN.name(), "0");
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.ranges = r2;
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("0")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }


    @Test
    public void testWithMatches1() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("OK")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithMatches2() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("good")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithMatches3() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("nice")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithMatches4() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("bad")).get(0);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    public void testWithMatches5() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("error")).get(0);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    public void noMatchIsEmpty() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        customKPI.init(kpiConfig);
        List<StatusValue> foo = customKPI.getStatusValues(getComponent("foo"));
        assertTrue(foo.isEmpty());
    }

    @Test
    public void testWithRangesAndMatches1() {
        CustomKPI customKPI = new CustomKPI();
        kpiConfig.matches = getMatches();
        kpiConfig.ranges = getRangeMap();
        customKPI.init(kpiConfig);
        StatusValue statusValue = customKPI.getStatusValues(getComponent("error")).get(0);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    public void testWithRangesAndMatches2() {
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