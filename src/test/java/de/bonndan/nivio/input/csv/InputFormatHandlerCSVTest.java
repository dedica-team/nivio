package de.bonndan.nivio.input.csv;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class InputFormatHandlerCSVTest {

    private FileFetcher fileFetcher;

    @BeforeEach
    public void setup() {
        fileFetcher = new FileFetcher(new HttpService());
    }

    @Test
    void read() throws MalformedURLException {

        File file1 = new File(getRootPath() + "/src/test/resources/example/services/test.csv");
        SourceReference file = new SourceReference(file1.toURI().toURL());

        Map<String, String> mapping = new HashMap<>();
        mapping.put("identifier", "1");
        mapping.put("name", "0");
        mapping.put("description", "2");
        file.setProperty("mapping", mapping);
        file.setProperty("skipLines", 1);
        file.setProperty("separator", ";");

        InputFormatHandlerCSV factoryCSV = new InputFormatHandlerCSV(fileFetcher);
        LandscapeDescription landscapeDescription = new LandscapeDescription("test");

        //when
        factoryCSV.applyData(file, landscapeDescription);

        assertEquals(3, landscapeDescription.getItemDescriptions().size());
        ItemDescription foo = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("foo", null, ItemDescription.class).orElseThrow();
        assertNotNull(foo);

        assertEquals("foo", foo.getIdentifier());
        assertTrue(foo.getLabels().containsKey("nivio.name"));
        assertEquals("foo", foo.getLabels().get("nivio.name"));
        assertTrue(foo.getLabels().containsKey("nivio.description"));
        assertEquals("This does nothing", foo.getLabels().get("nivio.description"));

        ItemDescription bar = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("bar", null, ItemDescription.class).orElseThrow();
        assertNotNull(bar);

        assertEquals("bar", bar.getIdentifier());
        assertTrue(bar.getLabels().containsKey("nivio.name"));
        assertEquals("bar", bar.getLabels().get("nivio.name"));
        assertTrue(bar.getLabels().containsKey("nivio.description"));
        assertEquals("", bar.getLabels().get("nivio.description"));

        ItemDescription super1 = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("super1", null, ItemDescription.class).orElseThrow();
        assertNotNull(super1);

        assertEquals("super1", super1.getIdentifier());
        assertTrue(super1.getLabels().containsKey("nivio.name"));
        assertEquals("Super Service", super1.getLabels().get("nivio.name"));
        assertTrue(super1.getLabels().containsKey("nivio.description"));
        assertEquals("superior", super1.getLabels().get("nivio.description"));
    }

    @Test
    void readRelationDescriptions() throws MalformedURLException {

        File file1 = new File(getRootPath() + "/src/test/resources/example/services/test_relation.csv");
        SourceReference file = new SourceReference(file1.toURI().toURL());

        Map<String, String> mapping = new HashMap<>();
        mapping.put("identifier", "1");
        mapping.put("name", "0");
        mapping.put("foo", "2");
        mapping.put("bar", "3");
        file.setProperty("mapping", mapping);
        file.setProperty("skipLines", 1);
        file.setProperty("separator", ";");

        InputFormatHandlerCSV factoryCSV = new InputFormatHandlerCSV(fileFetcher);
        LandscapeDescription landscapeDescription = new LandscapeDescription("test");

        //when
        factoryCSV.applyData(file, landscapeDescription);

        //then
        assertEquals(1, landscapeDescription.getItemDescriptions().size());
        ItemDescription foo = landscapeDescription.getIndexReadAccess().matchOneByIdentifiers("foo", null, ItemDescription.class).orElseThrow();
        assertNotNull(foo);

        assertThat(foo.getRelations()).hasSize(1).satisfies(relationDescriptions -> {
            RelationDescription next = relationDescriptions.iterator().next();
            assertThat(next.getSource()).isEqualTo("foo");
            assertThat(next.getTarget()).isEqualTo("bar");
            assertThat(next.getLabel("nivio.name")).isEqualTo("a relation");
            assertThat(next.getLabel("nivio.foo")).isEqualTo("This does nothing");
        });
    }

    @Test
    void failsWithoutMapping() throws MalformedURLException {

        File file1 = new File(getRootPath() + "/src/test/resources/example/services/test.csv");
        SourceReference file = new SourceReference(file1.toURI().toURL());
        InputFormatHandlerCSV factoryCSV = new InputFormatHandlerCSV(fileFetcher);

        assertThrows(ProcessingException.class, () -> {
            factoryCSV.applyData(file, new LandscapeDescription("test"));
        });
    }

    @Test
    void failsWithoutIdentifierInMapping() throws MalformedURLException {

        File file1 = new File(getRootPath() + "/src/test/resources/example/services/test.csv");
        SourceReference file = new SourceReference(file1.toURI().toURL());
        Map<String, String> mapping = new HashMap<>();
        mapping.put("name", "0");
        file.setProperty("mapping", mapping);

        InputFormatHandlerCSV factoryCSV = new InputFormatHandlerCSV(fileFetcher);

        assertThrows(ProcessingException.class, () -> {
            factoryCSV.applyData(file, new LandscapeDescription("test"));
        });
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
