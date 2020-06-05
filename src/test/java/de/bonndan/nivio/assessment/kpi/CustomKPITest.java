package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomKPITest {

    public static final String LABEL = "test";

    @Test
    public void testWithRanges1() {
        CustomKPI test = new CustomKPI(LABEL, null, getRangeMap(), null);
        test.init();
        StatusValue statusValue = test.getStatusValues(getComponent("2.58")).get(0);
        assertNotNull(statusValue);
        Assertions.assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithRanges2() {
        CustomKPI test = new CustomKPI(LABEL, null, getRangeMap(), null);
        test.init();
        StatusValue statusValue = test.getStatusValues(getComponent("0")).get(0);
        assertNotNull(statusValue);
        Assertions.assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithRanges3() {
        CustomKPI test = new CustomKPI(LABEL, null, getRangeMap(), null);
        test.init();
        StatusValue statusValue = test.getStatusValues(getComponent("10.1")).get(0);
        assertNotNull(statusValue);
        Assertions.assertEquals(Status.YELLOW, statusValue.getStatus());
    }

    @Test
    public void testoutOfRange() {
        CustomKPI test = new CustomKPI(LABEL, null, getRangeMap(), null);
        test.init();
        List<StatusValue> statusValues = test.getStatusValues(getComponent("100.1"));
        Assertions.assertTrue(statusValues.isEmpty());
    }

    @Test
    public void brokenRangeConfig() {
        Map<Status, String> rangeMap = getRangeMap();
        rangeMap.put(Status.GREEN, "0-12");
        CustomKPI customKPI = new CustomKPI(LABEL, null, rangeMap, null);

        assertThrows(ProcessingException.class, () -> customKPI.init());
    }

    @Test
    public void brokenMatchesConfig() {
        Map<Status, String> matches = getMatches();
        matches.put(Status.GREEN, "0-12[");
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, matches);

        assertThrows(ProcessingException.class, () -> customKPI.init());
    }

    @Test
    public void RangeOneNumber() {
        Map<Status, String> r2 = getRangeMap();
        r2.put(Status.GREEN, "0");

        CustomKPI customKPI = new CustomKPI(LABEL, null, r2, null);
        customKPI.init();
        StatusValue statusValue = customKPI.getStatusValues(getComponent("0")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }


    @Test
    public void testWithMatches1() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        customKPI.init();
        StatusValue statusValue = customKPI.getStatusValues(getComponent("OK")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithMatches2() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        customKPI.init();
        StatusValue statusValue = customKPI.getStatusValues(getComponent("good")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithMatches3() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        customKPI.init();
        StatusValue statusValue = customKPI.getStatusValues(getComponent("good")).get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
    }

    @Test
    public void testWithMatches4() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        customKPI.init();
        StatusValue statusValue = customKPI.getStatusValues(getComponent("bad")).get(0);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    public void testWithMatches5() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        customKPI.init();
        StatusValue statusValue = customKPI.getStatusValues(getComponent("error")).get(0);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    public void noMatchIsEmpty() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, null, getMatches());
        customKPI.init();
        List<StatusValue> foo = customKPI.getStatusValues(getComponent("foo"));
        assertTrue( foo.isEmpty());
    }

    @Test
    public void testWithRangesAndMatches1() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, getRangeMap(), getMatches());
        customKPI.init();
        StatusValue statusValue = customKPI.getStatusValues(getComponent("error")).get(0);
        assertEquals(Status.RED, statusValue.getStatus());
    }

    @Test
    public void testWithRangesAndMatches2() {
        CustomKPI customKPI = new CustomKPI(LABEL, null, getRangeMap(), getMatches());
        customKPI.init();
        StatusValue statusValue = customKPI.getStatusValues(getComponent("20.53")).get(0);
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