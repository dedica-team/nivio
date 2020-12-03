package de.bonndan.nivio.model;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Static factory to turn {@link Item}s into Lucene {@link Document}s.
 */
public class SearchDocumentFactory {

    public static final String LUCENE_FIELD_NAME = "name";
    public static final String LUCENE_FIELD_DESCRIPTION = "description";
    public static final String LUCENE_FIELD_CONTACT = "contact";
    public static final String LUCENE_FIELD_FQI = "fqi";
    public static final String LUCENE_FIELD_COMPONENT_TYPE = "component";
    public static final String LUCENE_FIELD_GROUP = "group";
    public static final String LUCENE_FIELD_ITEM_TYPE = "type";
    public static final String LUCENE_FIELD_OWNER = "owner";
    public static final String LUCENE_FIELD_TAG = "tag";
    private static final String LUCENE_FIELD_LIFECYCLE = Label.lifecycle.name();
    private static final String LUCENE_FIELD_CAPABILITY = Label.capability.name();
    private static final String LUCENE_FIELD_LAYER = Label.layer.name();

    public static FacetsConfig getConfig() {
        FacetsConfig config = new FacetsConfig();
        config.setMultiValued(LUCENE_FIELD_TAG, true);
        return config;
    }

    /**
     * Creates a new lucene document containing item fields and labels.
     *
     * @param item the item to index
     * @return searchable document
     */
    public static Document from(Item item) {
        Document document = new Document();
        document.add(new TextField(LUCENE_FIELD_COMPONENT_TYPE, "item", Field.Store.YES));
        document.add(new TextField(LUCENE_FIELD_FQI, item.getFullyQualifiedIdentifier().toString(), Field.Store.YES));

        document.add(new TextField(LUCENE_FIELD_NAME, Optional.ofNullable(item.getName()).orElse(""), Field.Store.YES));
        document.add(new TextField(LUCENE_FIELD_CONTACT, Optional.ofNullable(item.getContact()).orElse(""), Field.Store.YES));
        document.add(new TextField(LUCENE_FIELD_DESCRIPTION, Optional.ofNullable(item.getDescription()).orElse(""), Field.Store.YES));
        Optional.ofNullable(item.getGroup()).ifPresent(s -> document.add(new TextField(LUCENE_FIELD_GROUP, s, Field.Store.YES)));
        Optional.ofNullable(item.getType()).ifPresent(s -> document.add(new TextField(LUCENE_FIELD_ITEM_TYPE, s, Field.Store.YES)));
        Optional.ofNullable(item.getOwner()).ifPresent(s -> document.add(new TextField(LUCENE_FIELD_OWNER, s, Field.Store.YES)));

        //add all labels by their key
        item.getLabels().forEach((s, s2) -> {
            if (StringUtils.isEmpty(s2))
                return;
            document.add(new TextField(s, s2, Field.Store.YES));
        });

        //add links, title as key (duplicates are ok)
        item.getLinks().forEach((s, link) -> {
            if (link == null)
                return;
            String val = StringUtils.isEmpty(link.getName()) ? "" : link.getName() + " ";
            val += link.getHref();
            document.add(new TextField(s, val, Field.Store.YES));
        });

        //tags (searchable)
        Arrays.stream(item.getTags()).forEach(s -> {
            document.add(new TextField(LUCENE_FIELD_TAG, s.toLowerCase(), Field.Store.YES));
        });

        addFacets(document, item);
        return document;
    }

    /**
     * facets (categories)  not stored/searchable, but can be drilled down into
     *
     * @param document the doc to add facets to
     */
    private static void addFacets(Document document, Item item) {
        document.add(new FacetField(LUCENE_FIELD_COMPONENT_TYPE, "item"));

        //tag facets
        Arrays.stream(item.getTags()).forEach(s -> {
            document.add(new FacetField(LUCENE_FIELD_TAG, s.toLowerCase()));
        });

        Optional.ofNullable(item.getLabel(Label.lifecycle))
                .ifPresent(s -> document.add(new FacetField(LUCENE_FIELD_LIFECYCLE, s)));

        Optional.ofNullable(item.getLabel(Label.capability))
                .ifPresent(s -> document.add(new FacetField(LUCENE_FIELD_CAPABILITY, s)));

        Optional.ofNullable(item.getLabel(Label.layer))
                .ifPresent(s -> document.add(new FacetField(LUCENE_FIELD_LAYER, s)));
    }
}
