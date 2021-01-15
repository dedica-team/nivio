package de.bonndan.nivio.input.csv;

import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.observation.InputFormatObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class InputFormatHandlerCSVTest {

    private FileFetcher fileFetcher;

    @BeforeEach
    public void setup() {
        fileFetcher = new FileFetcher(new HttpService());
    }

    @Test
    public void read() {

        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/test.csv");

        Map<String, String> mapping = new HashMap<>();
        mapping.put("identifier", "1");
        mapping.put("name", "0");
        mapping.put("description", "2");
        file.setProperty("mapping", mapping);
        file.setProperty("skipLines", 1);
        file.setProperty("separator", ";");

        InputFormatHandlerCSV factoryCSV = new InputFormatHandlerCSV(fileFetcher);
        List<ItemDescription> services = factoryCSV.getDescriptions(file, null);

        assertEquals(3, services.size());
        ItemDescription foo = services.get(0);
        assertNotNull(foo);

        assertEquals("foo", foo.getIdentifier());
        assertTrue(foo.getLabels().containsKey("nivio.name"));
        assertEquals("foo", foo.getLabels().get("nivio.name"));
        assertTrue(foo.getLabels().containsKey("nivio.description"));
        assertEquals("This does nothing", foo.getLabels().get("nivio.description"));

        ItemDescription bar = services.get(1);
        assertNotNull(bar);

        assertEquals("bar", bar.getIdentifier());
        assertTrue(bar.getLabels().containsKey("nivio.name"));
        assertEquals("bar", bar.getLabels().get("nivio.name"));
        assertTrue(bar.getLabels().containsKey("nivio.description"));
        assertEquals("", bar.getLabels().get("nivio.description"));

        ItemDescription super1 = services.get(2);
        assertNotNull(super1);

        assertEquals("super1", super1.getIdentifier());
        assertTrue(super1.getLabels().containsKey("nivio.name"));
        assertEquals("Super Service", super1.getLabels().get("nivio.name"));
        assertTrue(super1.getLabels().containsKey("nivio.description"));
        assertEquals("superior", super1.getLabels().get("nivio.description"));
    }

    @Test
    public void failsWithoutMapping() {

        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/test.csv");
        InputFormatHandlerCSV factoryCSV = new InputFormatHandlerCSV(fileFetcher);

        assertThrows(ProcessingException.class, () -> {
            factoryCSV.getDescriptions(file, null);
        });
    }

    @Test
    public void failsWithoutIdentifierInMapping() {

        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/test.csv");
        Map<String, String> mapping = new HashMap<>();
        mapping.put("name", "0");
        file.setProperty("mapping", mapping);

        InputFormatHandlerCSV factoryCSV = new InputFormatHandlerCSV(fileFetcher);

        assertThrows(ProcessingException.class, () -> {
            factoryCSV.getDescriptions(file, null);
        });
    }

    @Test
    public void returnsUrlObserver() {
        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/test.csv");
        Map<String, String> mapping = new HashMap<>();
        mapping.put("name", "0");
        file.setProperty("mapping", mapping);

        InputFormatHandlerCSV factoryCSV = new InputFormatHandlerCSV(fileFetcher);

        //when
        InputFormatObserver observer = factoryCSV.getObserver(file, null);

        //then
        assertNotNull(observer);
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
