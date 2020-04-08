package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomKPITest {

    public static final String LABEL = "test";

    @Test
    public void testWithRanges1() {
        CustomKPI test = new CustomKPI(LABEL, null, getRangeMap(), null);
        StatusValue statusValue = test.getStatusValue(getComponent("2.58"));
        assertNotNull(statusValue);
        Assertions.assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithRanges2() {
        CustomKPI test = new CustomKPI(LABEL, null, getRangeMap(), null);
        StatusValue statusValue = test.getStatusValue(getComponent("0"));
        assertNotNull(statusValue);
        Assertions.assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithRanges3() {
        CustomKPI test = new CustomKPI(LABEL, null, getRangeMap(), null);
        StatusValue statusValue = test.getStatusValue(getComponent("10.1"));
        assertNotNull(statusValue);
        Assertions.assertEquals(Status.YELLOW, statusValue.getStatus());
    }

    @Test
    public void testoutOfRange() {
        CustomKPI test = new CustomKPI(LABEL, null, getRangeMap(), null);
        StatusValue statusValue = test.getStatusValue(getComponent("100.1"));
        assertNotNull(statusValue);
        Assertions.assertEquals(Status.UNKNOWN, statusValue.getStatus());
    }

    @Test
    public void brokenRangeConfig() {
        Map<Status, String> rangeMap = getRangeMap();
        rangeMap.put(Status.GREEN, "0-12");
        assertThrows(ProcessingException.class, () -> {new CustomKPI(LABEL, null, rangeMap, null);});
    }

    @Test
    public void RangeOneNumber() {
        Map<Status, String> r2 = getRangeMap();
        r2.put(Status.GREEN, "0");

        CustomKPI customKPI = new CustomKPI(LABEL, null, r2, null);
        StatusValue statusValue = customKPI.getStatusValue(getComponent("0"));
        assertEquals(Status.GREEN, statusValue.getStatus());
    }


    @Test
    public void testWithMatches1() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        StatusValue statusValue = customKPI.getStatusValue(getComponent("OK"));
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithMatches2() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        StatusValue statusValue = customKPI.getStatusValue(getComponent("good"));
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithMatches3() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        StatusValue statusValue = customKPI.getStatusValue(getComponent("good"));
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithMatches4() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        StatusValue statusValue = customKPI.getStatusValue(getComponent("bad"));
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    public void testWithMatches5() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        StatusValue statusValue = customKPI.getStatusValue(getComponent("error"));
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    public void noMatch() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        StatusValue statusValue = customKPI.getStatusValue(getComponent("foo"));
        assertEquals(Status.UNKNOWN, statusValue.getStatus());
    }

    @Test
    public void testWithRangesAndMatches1() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, getRangeMap(), getMatches());
        StatusValue statusValue = customKPI.getStatusValue(getComponent("error"));
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    public void testWithRangesAndMatches2() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, getRangeMap(), getMatches());
        StatusValue statusValue = customKPI.getStatusValue(getComponent("20.53"));
        assertEquals(Status.RED, statusValue.getStatus());
    }

    private Item getComponent(String value) {
        Item item = new Item();
        item.setLabel(LABEL, value);
        return item;
    }

    private Map<Status, String> getRangeMap() {
        Map<Status, String> rangeMap = new HashMap<>();
        rangeMap.put(Status.GREEN, "0;9.99999999");
        rangeMap.put(Status.YELLOW, "10;15");
        rangeMap.put(Status.ORANGE, "15;19.999999999");
        rangeMap.put(Status.RED, "20;40");
        return rangeMap;
    }

    private Map<Status, String> getMatches() {
        HashMap<Status, String> map = new HashMap<>();

        map.put(Status.GREEN, "OK;good;nice");
        map.put(Status.RED, "BAD;err.*");

        return map;
    }

}