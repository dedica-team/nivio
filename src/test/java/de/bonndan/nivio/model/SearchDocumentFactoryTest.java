package de.bonndan.nivio.model;

import org.apache.lucene.document.Document;
import org.junit.jupiter.api.Test;

import static de.bonndan.nivio.model.SearchDocumentFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class SearchDocumentFactoryTest {

    @Test
    public void generatesDocument() {
        //given
        Item item = new Item();
        item.setIdentifier("foo");
        item.setName("Hans");
        item.setDescription("Lorem ipsum");
        item.setContact("info@acme.com");

        //when
        Document document = SearchDocumentFactory.from(item);

        //then
        assertNotNull(document);
        assertEquals(item.getName(), document.get(LUCENE_FIELD_NAME));
        assertEquals(item.getContact(), document.get(LUCENE_FIELD_CONTACT));
        assertEquals(item.getDescription(), document.get(LUCENE_FIELD_DESCRIPTION));
    }
}