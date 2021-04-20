package de.bonndan.nivio.search;

import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.apache.lucene.document.Document;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static de.bonndan.nivio.search.SearchDocumentFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class SearchDocumentFactoryTest {

    @Test
    public void generatesDocument() throws MalformedURLException {
        //given
        Item item = getTestItemBuilder("agroup", "foo")
                .withName("Hans")
                .withDescription("Lorem ipsum")
                .withContact("info@acme.com")
                .withOwner("Marketing")
                .build();
        item.setLabel("foo", "bar");
        item.setLabel("foo2", "bar2");
        item.setLabel(Label.network + ".foonet", "foonet");
        item.setLabel(Label.network + ".barnet", "barnet");
        item.setLabel(Label.type, "app");
        item.setLink("wiki", new URL("http://foo.bar.baz"));
        item.setTags(new String[]{"one", "two"});

        //when
        Document document = SearchDocumentFactory.from(item);

        //then
        assertNotNull(document);
        assertEquals(item.getIdentifier(), document.get(LUCENE_FIELD_IDENTIFIER));
        assertEquals(item.getName(), document.get(LUCENE_FIELD_NAME));
        assertEquals(item.getContact(), document.get(LUCENE_FIELD_CONTACT));
        assertEquals(item.getDescription(), document.get(LUCENE_FIELD_DESCRIPTION));
        assertEquals(item.getOwner(), document.get(LUCENE_FIELD_OWNER));

        assertEquals(item.getLabel("foo"), document.get("foo"));
        assertEquals(item.getLabel("foo2"), document.get("foo2"));
        assertEquals(item.getLabel(Label.type), document.get("type"));

        assertEquals(item.getLinks().get("wiki").getHref().toString(), document.get("wiki"));
        String[] tag = document.getValues("tag");
        List<String> tags = List.of(tag);
        assertTrue(tags.contains("one"));
        assertTrue(tags.contains("two"));

        String[] network = document.getValues("network");
        List<String> networks = List.of(network);
        assertTrue(networks.contains("foonet"));
        assertTrue(networks.contains("barnet"));
    }
}