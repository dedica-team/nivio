package de.bonndan.nivio.model;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import java.util.Optional;

/**
 * Static factory to turn {@link Item}s into Lucene {@link Document}s.
 *
 *
 */
public class SearchDocumentFactory {

    public static final String LUCENE_FIELD_NAME = "name";
    public static final String LUCENE_FIELD_DESCRIPTION = "description";
    public static final String LUCENE_FIELD_CONTACT = "contact";
    public static final String LUCENE_FIELD_FQI = "fqi";

    public static Document from(Item item) {
        Document document = new Document();
        document.add(new TextField(LUCENE_FIELD_FQI, item.getFullyQualifiedIdentifier().toString(), Field.Store.YES));
        document.add(new TextField(LUCENE_FIELD_NAME, Optional.ofNullable(item.getName()).orElse(""), Field.Store.YES));
        document.add(new TextField(LUCENE_FIELD_CONTACT, Optional.ofNullable(item.getContact()).orElse(""), Field.Store.YES));
        document.add(new TextField(LUCENE_FIELD_DESCRIPTION, Optional.ofNullable(item.getDescription()).orElse(""), Field.Store.YES));

        return document;
    }
}
