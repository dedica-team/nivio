package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.model.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LabelToFieldResolverTest {

    private LabelToFieldResolver processor;

    @BeforeEach
    public void setup() {
        Logger logger = LoggerFactory.getLogger(LabelToFieldResolverTest.class);
        ProcessLog processLog = new ProcessLog(logger, "test");
        processor = new LabelToFieldResolver(processLog);
    }

    @Test
    @DisplayName("Ensure comma separated strings are parsed properly")
    void process() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.name", "foo");
        item1.getLabels().put("NiVIO.description", "bar");
        item1.getLabels().put("NIVIO.providedBy", "baz, bak");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(item1);

        //when
        processor.resolve(input);

        //then
        assertEquals("foo", item1.getName());
        assertEquals("bar", item1.getDescription());
        assertEquals(2, item1.getProvidedBy().size());
    }


    @Test
    @DisplayName("Prefixed labels are removed after processing")
    void cleanup() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.name", "foo");
        item1.getLabels().put("nivio.description", "bar");
        item1.getLabels().put("nivio.providedBy", "baz, bak");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(item1);

        assertEquals(4, item1.getLabels().size());

        //when
        processor.resolve(input);

        //then
        assertEquals(1, item1.getLabels().size()); //"a" remains
    }

    @Test
    @DisplayName("Ensure providedBy label is case insensitive")
    void providedbyLowercase() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("NIVIO.providedby", "baz ");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(item1);

        //when
        processor.resolve(input);

        //then
        assertEquals(1, item1.getProvidedBy().size());
    }

    @Test
    @DisplayName("Ensure relations can be set via labels")
    void relations() {
        ItemDescription item1 = new ItemDescription("test");
        item1.getLabels().put("nivio.relations", "bar, baz");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(item1);

        //when
        processor.resolve(input);

        //then
        assertEquals(2, item1.getRelations().size());
        RelationDescription[] actual = item1.getRelations().toArray(RelationDescription[]::new);
        boolean matchesBar = Arrays.stream(actual).anyMatch(relationDescription -> relationDescription.getTarget().equals("bar"));
        assertThat(matchesBar).isTrue();
        boolean matchesBaz = Arrays.stream(actual).anyMatch(relationDescription -> relationDescription.getTarget().equals("baz"));
        assertThat(matchesBaz).isTrue();
    }

    @Test
    @DisplayName("Ensure provider relations can be set via labels")
    void extendedRelationsProvider() {
        ItemDescription foo = new ItemDescription();
        foo.setIdentifier("foo");
        foo.getLabels().put("nivio.relations.provider", "bar");

        ItemDescription bar = new ItemDescription();
        bar.setIdentifier("bar");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(foo);
        input.getWriteAccess().addOrReplaceChild(bar);

        //when
        processor.resolve(input);

        //then
        assertEquals(1, foo.getRelations().size());
        RelationDescription[] actual = foo.getRelations().toArray(RelationDescription[]::new);

        Optional<RelationDescription> rel = Arrays.stream(actual).filter(description -> description.getSource().equals("bar")).findFirst();
        assertThat(rel).isPresent().map(RelationDescription::getType).get().isEqualTo(RelationType.PROVIDER);
    }

    @Test
    @DisplayName("Ensure inbound relations can be set via labels")
    void extendedRelationsInbound() {
        ItemDescription foo = new ItemDescription();
        foo.setIdentifier("foo");
        foo.getLabels().put("nivio.relations.inbound", "bar");

        ItemDescription bar = new ItemDescription();
        bar.setIdentifier("bar");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(foo);
        input.getWriteAccess().addOrReplaceChild(bar);

        //when
        processor.resolve(input);

        //then
        assertEquals(1, foo.getRelations().size());
        Optional<RelationDescription> rel = foo.getRelations().stream().findFirst();
        assertThat(rel).isPresent();
        assertThat(rel.get().getSource()).isEqualTo("bar");
    }

    @Test
    @DisplayName("Ensure comma separated strings are parsed properly")
    void listFieldLabelWithoutDelimiter() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.name", "foo");
        item1.getLabels().put("NiVIO.description", "bar");
        item1.getLabels().put("NIVIO.providedBy", "baz ");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(item1);

        //when
        processor.resolve(input);

        //then
        assertEquals("foo", item1.getName());
        assertEquals("bar", item1.getDescription());
        assertEquals(1, item1.getProvidedBy().size());
    }

    @Test
    @DisplayName("Ensure comma separated links are parsed properly")
    void deprecatedLinks() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.links", "http://one.com, https://two.net");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(item1);

        //when
        processor.resolve(input);

        //then
        Map<String, Link> links = item1.getLinks();
        assertFalse(links.isEmpty());
        Link link = links.get("1");
        assertNotNull(link);
        assertNotNull(link.getHref());
        assertEquals("http://one.com", link.getHref().toString());

        link = links.get("2");
        assertNotNull(link);
        assertNotNull(link.getHref());
        assertEquals("https://two.net", link.getHref().toString());
    }

    @Test
    @DisplayName("Ensure links with keys are parsed")
    void links() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.link.wiki", "http://one.com");
        item1.getLabels().put("nivio.link.repo", "https://two.net");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(item1);

        //when
        processor.resolve(input);

        //then
        Map<String, Link> links = item1.getLinks();
        assertFalse(links.isEmpty());
        Link url = links.get("wiki");
        assertNotNull(url);
        assertNotNull(url.getHref());
        assertEquals("http://one.com", url.getHref().toString());

        url = links.get("repo");
        assertNotNull(url);
        assertNotNull(url.getHref());
        assertEquals("https://two.net", url.getHref().toString());
    }

    @Test
    @DisplayName("Ensure frameworks with a map structure are parsed")
    void frameworks() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio." + Label.framework.name() + ".java", "8");
        item1.getLabels().put("nivio." + Label.framework.name() + ".angular", "6");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(item1);

        //when
        processor.resolve(input);

        //then
        Map<String, String> frameworks = item1.getLabels(Label.framework);
        assertFalse(frameworks.isEmpty());
        assertThat(frameworks).containsKey("framework.java");
        assertThat(frameworks.get("framework.java")).isEqualTo("8");
        assertThat(frameworks).containsKey("framework.angular");
        assertThat(frameworks.get("framework.angular")).isEqualTo("6");
    }

    @Test
    @DisplayName("Ensure comma separated frameworks are parsed properly")
    void commaSeparatedFrameworks() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio." + Label.frameworks.name(), "java:8, angular:6");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(item1);

        //when
        processor.resolve(input);

        //then
        Map<String, String> frameworks = item1.getLabels(Label.framework);
        assertFalse(frameworks.isEmpty());
        assertThat(frameworks).containsKey("framework.java");
        assertThat(frameworks.get("framework.java")).isEqualTo("8");
        assertThat(frameworks).containsKey("framework.angular");
        assertThat(frameworks.get("framework.angular")).isEqualTo("6");
    }

    @Test
    @DisplayName("Ensure comma separated links are parsed properly")
    void labelsToLabels() {
        ItemDescription item1 = new ItemDescription();
        item1.getLabels().put("a", "b");
        item1.getLabels().put("nivio.visibility", "public");
        item1.getLabels().put("nivio.software", "wordpress");
        item1.getLabels().put("nivio.other", "foo");

        LandscapeDescription input = new LandscapeDescription("identifier", "name", null);
        input.getWriteAccess().addOrReplaceChild(item1);

        //when
        processor.resolve(input);

        //then
        String vis = item1.getLabel(Label.visibility);
        assertNotNull(vis);
        assertEquals("public", vis);

        String software = item1.getLabel(Label.software);
        assertNotNull(software);
        assertEquals("wordpress", software);

        String other = item1.getLabel("other");
        assertNotNull(other);
        assertEquals("foo", other);
    }
}
