package de.bonndan.nivio.search;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.SearchDocumentValueObjectFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.index.IndexableField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.bonndan.nivio.search.SearchDocumentFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchDocumentFactoryTest {

    private Item item;
    private SearchDocumentValueObject valueObject;

    @BeforeEach
    void setup() throws MalformedURLException {
        var graph = new GraphTestSupport();
        item = graph.getTestItemBuilder(graph.groupA.getIdentifier(), "foo")
                .withName("Hans")
                .withDescription("Lorem ipsum")
                .withContact("info@acme.com")
                .withOwner("Marketing")
                .withType("app")
                .build();
        item.setLabel("foo", "bar");
        item.setLabel("foo2", "bar2");
        item.setLabel(Label.network + ".foonet", "foonet");
        item.setLabel(Label.network + ".barnet", "barnet");
        item.setLink("wiki", new URL("http://foo.bar.baz"));
        item.setTags(new String[]{"one", "two"});
        item.setLabel(Label.framework.withPrefix("java"), "8");
        item.setLabel(Label.framework.withPrefix("Spring Boot"), "2.0.1");
        graph.landscape.getWriteAccess().addOrReplaceChild(item);

        valueObject = SearchDocumentValueObjectFactory.createFor(item);
    }

    @Test
    void generatesDocument() {

        //when
        Document document = SearchDocumentFactory.from(valueObject, List.of());

        //then
        assertNotNull(document);
        assertEquals(item.getIdentifier(), document.get(SearchField.IDENTIFIER.getValue()));
        assertEquals(item.getName(), document.get(SearchField.LUCENE_FIELD_NAME.getValue()));
        assertEquals(item.getDescription(), document.get(SearchField.LUCENE_FIELD_DESCRIPTION.getValue()));
        assertEquals(item.getOwner(), document.get(SearchField.LUCENE_FIELD_OWNER.getValue()));

        assertEquals(item.getLabel("foo"), document.get("foo"));
        assertEquals(item.getLabel("foo2"), document.get("foo2"));
        assertEquals(item.getType(), document.get(SearchField.LUCENE_FIELD_TYPE.getValue()));
        assertThat(document.get(SearchField.LUCENE_FIELD_LAYER.getValue())).isEqualTo(item.getLayer());
        assertThat(document.get(SearchField.LUCENE_FIELD_ADDRESS.getValue())).isEqualTo(item.getAddress());

        String wikiLink = item.getLinks().get("wiki").getHref().toString();
        assertEquals(wikiLink, document.get("wiki"));
        String[] tag = document.getValues("tag");
        List<String> tags = List.of(tag);
        assertTrue(tags.contains("one"));
        assertTrue(tags.contains("two"));

        String[] network = document.getValues(SearchField.LUCENE_FIELD_NETWORK.getValue());
        List<String> networks = List.of(network);
        assertTrue(networks.contains("foonet"));
        assertTrue(networks.contains("barnet"));

        List<String> frameworksValue = Arrays.asList(document.getValues(SearchField.LUCENE_FIELD_FRAMEWORK.getValue()));
        assertThat(frameworksValue).contains("java").contains("spring boot");
        //per-framework field
        List<String> frameworks = document.getFields().stream().map(indexableField -> indexableField.name()).collect(Collectors.toList());
        assertThat(frameworks).contains("java");
        String javaVersion = Arrays.stream(document.getValues("java")).findFirst().orElseThrow();
        assertThat(javaVersion).isEqualTo("8");

        String genericField = document.get(SearchField.LUCENE_FIELD_GENERIC.getValue());
        assertThat(genericField).contains("java").contains("spring").contains("boot");
        assertThat(genericField).contains("bar2"); //label
        assertThat(genericField).doesNotContain("2.0.1"); //not framework version/value
    }

    @Test
    void addsFacets() {
        //when
        Document document = SearchDocumentFactory.from(valueObject, new ArrayList<StatusValue>());

        //then

        FacetField unitFacet = getField(SearchField.LUCENE_FIELD_UNIT.getValue(), document.getFields());
        assertThat(unitFacet).isNotNull();
        assertThat(unitFacet.path[0]).isEqualTo("default");

        FacetField contextFacet = getField(SearchField.LUCENE_FIELD_CONTEXT.getValue(), document.getFields());
        assertThat(contextFacet).isNotNull();
        assertThat(contextFacet.path[0]).isEqualTo("default");

        FacetField groupFacet = getField(SearchField.LUCENE_FIELD_GROUP.getValue(), document.getFields());
        assertThat(groupFacet).isNotNull();
        assertThat(groupFacet.path[0]).isEqualTo("a");


    }

    @Test
    void addsKPIFacets() {
        //given
        List<StatusValue> statusValues = new ArrayList<>();
        StatusValue foo = new StatusValue(item.getFullyQualifiedIdentifier(), "foo", Status.RED, "xyz");
        statusValues.add(foo);
        StatusValue bar = new StatusValue(item.getFullyQualifiedIdentifier(), "bar", Status.GREEN, "bar");
        statusValues.add(bar);
        StatusValue summary = StatusValue.summary(item.getFullyQualifiedIdentifier(), new ArrayList<>(List.of(foo, bar)));
        statusValues.add(summary);

        //when
        Document document = SearchDocumentFactory.from(valueObject, statusValues);

        //then
        assertNotNull(document);
        FacetField fooFacet = getField(KPI_FACET_PREFIX + "foo", document.getFields());
        assertThat(fooFacet).isNotNull();
        assertThat(fooFacet.path[0]).isEqualTo("RED");

        FacetField barFacet = getField(KPI_FACET_PREFIX + "bar", document.getFields());
        assertThat(barFacet).isNotNull();
        assertThat(barFacet.path[0]).isEqualTo("GREEN");

        FacetField summaryFacet = getField(KPI_FACET_PREFIX + StatusValue.SUMMARY_FIELD_VALUE, document.getFields());
        assertThat(summaryFacet).isNotNull();
        assertThat(summaryFacet.path[0]).isEqualTo("RED");
    }

    private FacetField getField(String s, List<IndexableField> fields) {
        for (IndexableField field : fields) {
            if (field instanceof FacetField) {
                if (((FacetField) field).dim.equals(s)) {
                    return (FacetField) field;
                }
            }
        }

        return null;
    }
}