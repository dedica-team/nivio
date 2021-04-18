package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Static factory to turn {@link Item}s into Lucene {@link Document}s.
 */
public class SearchDocumentFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchDocumentFactory.class);

    public static final String LUCENE_FIELD_IDENTIFIER = "identifier";
    public static final String LUCENE_FIELD_NAME = "name";
    public static final String LUCENE_FIELD_DESCRIPTION = "description";
    public static final String LUCENE_FIELD_CONTACT = "contact";
    public static final String LUCENE_FIELD_FQI = "fqi";
    public static final String LUCENE_FIELD_COMPONENT_TYPE = "component";
    public static final String LUCENE_FIELD_GROUP = "group";
    public static final String LUCENE_FIELD_ITEM_TYPE = "type";
    public static final String LUCENE_FIELD_OWNER = "owner";
    public static final String LUCENE_FIELD_TAG = "tag";
    public static final String LUCENE_FIELD_NETWORK = Label.network.name();
    private static final String LUCENE_FIELD_LIFECYCLE = Label.lifecycle.name();
    private static final String LUCENE_FIELD_CAPABILITY = Label.capability.name();
    private static final String LUCENE_FIELD_LAYER = Label.layer.name();
    public static final String KPI_FACET_PREFIX = "kpi.";

    public static FacetsConfig getConfig() {
        FacetsConfig config = new FacetsConfig();
        config.setMultiValued(LUCENE_FIELD_TAG, true);
        config.setMultiValued(LUCENE_FIELD_NETWORK, true);
        return config;
    }

    /**
     * Creates a new lucene document containing item fields and labels.
     *
     * @param item         the item to index
     * @param statusValues kpi values for the item
     * @return searchable document
     */
    @NonNull
    public static Document from(@NonNull final Item item, @Nullable List<StatusValue> statusValues) {
        Objects.requireNonNull(item, "Item to build search document from is null");
        if (statusValues == null) {
            statusValues = new ArrayList<>();
        }

        Document document = new Document();

        BiConsumer<String, String> addTextField = (field, value) -> Optional.ofNullable(value)
                .ifPresentOrElse(
                        val -> document.add(new TextField(field, val, Field.Store.YES)),
                        () -> document.add(new TextField(field, "", Field.Store.YES))
                );

        addTextField.accept(LUCENE_FIELD_COMPONENT_TYPE, "item");
        addTextField.accept(LUCENE_FIELD_FQI, item.getFullyQualifiedIdentifier().toString());
        addTextField.accept(LUCENE_FIELD_IDENTIFIER, item.getIdentifier());
        addTextField.accept(LUCENE_FIELD_NAME, item.getName());
        addTextField.accept(LUCENE_FIELD_CONTACT, item.getContact());
        addTextField.accept(LUCENE_FIELD_DESCRIPTION, item.getDescription());
        addTextField.accept(LUCENE_FIELD_GROUP, item.getGroup());
        addTextField.accept(LUCENE_FIELD_ITEM_TYPE, item.getType());
        addTextField.accept(LUCENE_FIELD_OWNER, item.getOwner());

        //add all labels by their key
        item.getLabels().forEach((labelKey, val) -> {
            if (StringUtils.isEmpty(val))
                return;
            addTextField.accept(labelKey, val);
        });

        //add links, title as key (duplicates are ok)
        item.getLinks().forEach((s, link) -> {
            if (link == null)
                return;
            String val = StringUtils.isEmpty(link.getName()) ? "" : link.getName() + " ";
            val += link.getHref();
            addTextField.accept(s, val);
        });

        //tags (searchable)
        Arrays.stream(item.getTags()).forEach(tag -> addTextField.accept(LUCENE_FIELD_TAG, tag.toLowerCase(Locale.ROOT)));

        //networks
        item.getLabels(Label.network).forEach((key, value) -> addTextField.accept(LUCENE_FIELD_NETWORK, value.toLowerCase(Locale.ROOT)));

        addFacets(document, item, statusValues);
        return document;
    }

    /**
     * facets (categories)  not stored/searchable, but can be drilled down into
     *
     * @param document     the doc to add facets to
     * @param statusValues kpi values for the item
     */
    private static void addFacets(final Document document, final Item item, List<StatusValue> statusValues) {

        BiConsumer<String, String> addFacetField = (field, value) ->
                Optional.ofNullable(value).ifPresent(val -> {
                    if (field != null) {
                        LOGGER.debug("Adding facet {} to document {}", field, item.getFullyQualifiedIdentifier());
                        document.add(new FacetField(field, val));
                    }
                });

        //tag facets
        Arrays.stream(item.getTags())
                .forEach(tag -> addFacetField.accept(LUCENE_FIELD_TAG, tag.toLowerCase(Locale.ROOT)));

        //network facets
        item.getLabels(Label.network)
                .forEach((key, value) -> addFacetField.accept(LUCENE_FIELD_NETWORK, value.toLowerCase(Locale.ROOT)));

        addFacetField.accept(LUCENE_FIELD_LIFECYCLE, item.getLabel(Label.lifecycle));
        addFacetField.accept(LUCENE_FIELD_CAPABILITY, item.getLabel(Label.capability));
        addFacetField.accept(LUCENE_FIELD_LAYER, item.getLabel(Label.layer));
        addFacetField.accept(LUCENE_FIELD_OWNER, item.getOwner());
        addFacetField.accept(LUCENE_FIELD_GROUP, item.getGroup());
        addFacetField.accept(LUCENE_FIELD_ITEM_TYPE, item.getType());

        //kpis, facets are prefixed to prevent name collisions (kpis can have any names)
        statusValues.forEach(statusValue -> {
            final String field = statusValue.getField().startsWith(StatusValue.SUMMARY_LABEL) ?
                    StatusValue.SUMMARY_LABEL : statusValue.getField();
            addFacetField.accept(KPI_FACET_PREFIX + field, statusValue.getStatus().getName());
        });
    }
}
