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

    private SearchDocumentFactory() {
    }

    public static final String LUCENE_FIELD_IDENTIFIER = "identifier";
    public static final String LUCENE_FIELD_PARENT_IDENTIFIER = "parentIdentifier";
    public static final String LUCENE_FIELD_NAME = "name";
    public static final String LUCENE_FIELD_DESCRIPTION = "description";

    /**
     * This is used to collect strings which should be directly searchable
     */
    public static final String LUCENE_FIELD_GENERIC = "generic";
    public static final String LUCENE_FIELD_FQI = "fqi";
    public static final String LUCENE_FIELD_COMPONENT = "component";
    public static final String LUCENE_FIELD_GROUP = "group";
    public static final String LUCENE_FIELD_TYPE = "type";
    public static final String LUCENE_FIELD_OWNER = "owner";
    public static final String LUCENE_FIELD_TAG = "tag";
    public static final String LUCENE_FIELD_NETWORK = Label.network.name();
    public static final String LUCENE_FIELD_LIFECYCLE = Label.lifecycle.name();
    public static final String LUCENE_FIELD_CAPABILITY = Label.capability.name();
    public static final String LUCENE_FIELD_LAYER = "layer";
    public static final String LUCENE_FIELD_ADDRESS = "address";
    public static final String LUCENE_FIELD_FRAMEWORK = Label.framework.name();
    public static final String KPI_FACET_PREFIX = "kpi_";

    public static FacetsConfig getConfig() {
        FacetsConfig config = new FacetsConfig();
        config.setMultiValued(LUCENE_FIELD_TAG, true);
        config.setMultiValued(LUCENE_FIELD_NETWORK, true);
        config.setMultiValued(LUCENE_FIELD_FRAMEWORK, true);
        return config;
    }

    /**
     * Creates a new lucene document containing item fields and labels.
     *
     * @param valueObject    the item to index
     * @param statusValues kpi values for the item
     * @return searchable document
     */
    @NonNull
    public static Document from(@NonNull final SearchDocumentValueObject valueObject,
                                @Nullable List<StatusValue> statusValues
    ) {
        Objects.requireNonNull(valueObject, "component to build search document from is null");
        if (statusValues == null) {
            statusValues = new ArrayList<>();
        }

        Document document = new Document();
        BiConsumer<String, String> addTextField = (field, value) -> Optional.ofNullable(value)
                .ifPresentOrElse(
                        val -> document.add(new TextField(field, val, Field.Store.YES)),
                        () -> document.add(new TextField(field, "", Field.Store.YES))
                );

        addTextField.accept(LUCENE_FIELD_COMPONENT, valueObject.getComponent());
        addTextField.accept(LUCENE_FIELD_FQI, valueObject.getFullyQualifiedIdentifier().toString());
        addTextField.accept(LUCENE_FIELD_IDENTIFIER, valueObject.getIdentifier());
        addTextField.accept(LUCENE_FIELD_PARENT_IDENTIFIER, valueObject.getParentIdentifier());
        addTextField.accept(LUCENE_FIELD_NAME, valueObject.getName());
        addTextField.accept(LUCENE_FIELD_DESCRIPTION, valueObject.getDescription());
        addTextField.accept(LUCENE_FIELD_TYPE, valueObject.getType());
        addTextField.accept(LUCENE_FIELD_OWNER, valueObject.getOwner());

        valueObject.getLayer().ifPresent(s -> addTextField.accept(LUCENE_FIELD_LAYER, s));
        valueObject.getGroup().ifPresent(s -> addTextField.accept(LUCENE_FIELD_GROUP, s));
        valueObject.getAddress().ifPresent(s -> addTextField.accept(LUCENE_FIELD_ADDRESS, s));

        List<String> genericStrings = new ArrayList<>();
        //add all labels by their key
        valueObject.getLabels().forEach((labelKey, val) -> {
            if (!StringUtils.hasLength(val))
                return;
            addTextField.accept(labelKey, val);

            //add non-prefixed label values to generic field
            if (!labelKey.contains(Label.DELIMITER)) {
                genericStrings.add(val);
            }
        });

        //add links, title as key (duplicates are ok)
        valueObject.getLinks().forEach((s, link) -> {
            if (link == null)
                return;
            String val = !StringUtils.hasLength(link.getName()) ? "" : link.getName() + " ";
            val += link.getHref();
            addTextField.accept(s, val);
        });

        //tags (searchable)
        Arrays.stream(valueObject.getTags())
                .map(tag -> tag.toLowerCase(Locale.ROOT))
                .forEach(tag -> addTextField.accept(LUCENE_FIELD_TAG, tag));

        //networks
        valueObject.getLabels(Label.network).forEach((key, value) -> addTextField.accept(LUCENE_FIELD_NETWORK, value.toLowerCase(Locale.ROOT)));

        //frameworks
        List<String> frameworks = new ArrayList<>();
        valueObject.getLabels(Label.framework).forEach((key, value) -> {
            String val = value.toLowerCase(Locale.ROOT);
            String unprefixed = Label.framework.unprefixed(key);
            frameworks.add(unprefixed);
            addTextField.accept(unprefixed, val);
            genericStrings.add(unprefixed);
        });
        frameworks.forEach(s -> addTextField.accept(LUCENE_FIELD_FRAMEWORK, s));

        //kpis, fields are prefixed to prevent name collisions (kpis can have any names)
        statusValues.forEach(statusValue -> {
            addTextField.accept(KPI_FACET_PREFIX + statusValue.getField(), statusValue.getStatus().getName());
        });

        //frameworks name (label keys)
        addTextField.accept(LUCENE_FIELD_GENERIC, StringUtils.collectionToDelimitedString(genericStrings, " "));

        addFacets(document, valueObject, statusValues);
        return document;
    }

    /**
     * facets (categories)  not stored/searchable, but can be drilled down into
     *
     * @param document      the doc to add facets to
     * @param valueProvider component values
     * @param statusValues  kpi values for the item
     */
    private static void addFacets(final Document document,
                                  final SearchDocumentValueObject valueProvider,
                                  final List<StatusValue> statusValues
    ) {

        BiConsumer<String, String> addFacetField = (field, value) ->
                Optional.ofNullable(value).ifPresent(val -> {
                    if (field != null) {
                        LOGGER.debug("Adding facet {} to document {}", field, valueProvider.getFullyQualifiedIdentifier());
                        document.add(new FacetField(field, val));
                    }
                });

        //tag facets
        Arrays.stream(valueProvider.getTags())
                .forEach(tag -> addFacetField.accept(LUCENE_FIELD_TAG, tag.toLowerCase(Locale.ROOT)));

        //network facets
        valueProvider.getLabels(Label.network)
                .forEach((key, value) -> addFacetField.accept(LUCENE_FIELD_NETWORK, value.toLowerCase(Locale.ROOT)));

        //framework facets (only key, not version)
        valueProvider.getLabels(Label.framework)
                .forEach((key, value) -> addFacetField.accept(LUCENE_FIELD_FRAMEWORK, Label.framework.unprefixed(key)));

        addFacetField.accept(LUCENE_FIELD_LIFECYCLE, valueProvider.getLabels().get(Label.lifecycle.name()));
        addFacetField.accept(LUCENE_FIELD_CAPABILITY, valueProvider.getLabels().get(Label.capability.name()));
        addFacetField.accept(LUCENE_FIELD_OWNER, valueProvider.getOwner());
        addFacetField.accept(LUCENE_FIELD_TYPE, valueProvider.getType());

        valueProvider.getGroup().ifPresent(s -> addFacetField.accept(LUCENE_FIELD_GROUP, s));
        valueProvider.getLayer().ifPresent(s -> addFacetField.accept(LUCENE_FIELD_LAYER, s));

        //kpis, facets are prefixed to prevent name collisions (kpis can have any names)
        statusValues.forEach(statusValue -> {
            addFacetField.accept(KPI_FACET_PREFIX + statusValue.getField(), statusValue.getStatus().getName());
        });
    }
}