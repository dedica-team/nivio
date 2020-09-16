package de.bonndan.nivio.model;

import org.apache.lucene.document.Document;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static de.bonndan.nivio.model.SearchDocumentFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class SearchDocumentFactoryTest {

    @Test
    public void generatesDocument() throws MalformedURLException {
        //given
        Item item = new Item(null, "foo");
        item.setName("Hans");
        item.setDescription("Lorem ipsum");
        item.setContact("info@acme.com");
        item.setLabel("foo", "bar");
        item.setLabel("foo2", "bar2");
        item.setLink("wiki", new URL("http://foo.bar.baz"));
        item.setTags(new String[]{"one", "two"});

        //when
        Document document = SearchDocumentFactory.from(item);

        //then
        assertNotNull(document);
        assertEquals(item.getName(), document.get(LUCENE_FIELD_NAME));
        assertEquals(item.getContact(), document.get(LUCENE_FIELD_CONTACT));
        assertEquals(item.getDescription(), document.get(LUCENE_FIELD_DESCRIPTION));

        assertEquals(item.getLabel("foo"), document.get("foo"));
        assertEquals(item.getLabel("foo2"), document.get("foo2"));

        assertEquals(item.getLinks().get("wiki").getHref().toString(), document.get("wiki"));
        String[] tag = document.getValues("tag");
        List<String> tags = List.of(tag);
        assertTrue(tags.contains("one"));
        assertTrue(tags.contains("two"));
    }
}