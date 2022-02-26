package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.apache.lucene.document.*;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.IndexOptions;
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

    public static final String KPI_FACET_PREFIX = "kpi_";

    public static FacetsConfig getConfig() {
        FacetsConfig config = new FacetsConfig();
        config.setMultiValued(SearchField.LUCENE_FIELD_TAG.getValue(), true);
        config.setMultiValued(SearchField.LUCENE_FIELD_NETWORK.getValue(), true);
        config.setMultiValued(SearchField.LUCENE_FIELD_FRAMEWORK.getValue(), true);
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

        //tokenized text
        BiConsumer<String, String> addTextField = (field, value) -> Optional.ofNullable(value)
                .ifPresentOrElse(
                        val -> document.add(new TextField(field, val, Field.Store.NO)),
                        () -> document.add(new TextField(field, "", Field.Store.NO))
                );

        BiConsumer<String, String> storedString = (field, value) -> Optional.ofNullable(value)
                .ifPresentOrElse(
                        val -> document.add(new StringField(field, val, Field.Store.YES)),
                        () -> document.add(new StringField(field, "", Field.Store.YES))
                );

        //single words, keeps numbers
        BiConsumer<String, String> addStringField = (field, value) -> Optional.ofNullable(value)
                .ifPresentOrElse(
                        val -> {
                            document.add(new StringField(field, val, Field.Store.NO)); //for exact matching

                            var type = new FieldType(); //like textfield, for searching
                            type.setTokenized(true);
                            type.setIndexOptions(IndexOptions.DOCS);
                            document.add(new Field(field, val, type));
                        },
                        () -> document.add(new StringField(field, "", Field.Store.NO))
                );

        storedString.accept(SearchField.LUCENE_FIELD_FQI.getValue(), valueObject.getFullyQualifiedIdentifier().toString());

        addStringField.accept(SearchField.LUCENE_FIELD_COMPONENT.getValue(), valueObject.getComponentClass().name());
        addStringField.accept(SearchField.IDENTIFIER.getValue(), valueObject.getIdentifier());
        addStringField.accept(SearchField.PARENT_IDENTIFIER.getValue(), valueObject.getParentIdentifier());

        addTextField.accept(SearchField.LUCENE_FIELD_NAME.getValue(), valueObject.getName());
        addTextField.accept(SearchField.LUCENE_FIELD_DESCRIPTION.getValue(), valueObject.getDescription());
        addTextField.accept(SearchField.LUCENE_FIELD_TYPE.getValue(), valueObject.getType());
        addTextField.accept(SearchField.LUCENE_FIELD_OWNER.getValue(), valueObject.getOwner());

        valueObject.getLayer().ifPresent(s -> addTextField.accept(SearchField.LUCENE_FIELD_LAYER.getValue(), s));
        valueObject.getGroup().ifPresent(s -> addTextField.accept(SearchField.LUCENE_FIELD_GROUP.getValue(), s));
        valueObject.getAddress().ifPresent(s -> addTextField.accept(SearchField.LUCENE_FIELD_ADDRESS.getValue(), s));

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
                .forEach(tag -> addStringField.accept(SearchField.LUCENE_FIELD_TAG.getValue(), tag));

        //networks
        valueObject.getLabels(Label.network).forEach((key, value) -> addStringField.accept(SearchField.LUCENE_FIELD_NETWORK.getValue(), value.toLowerCase(Locale.ROOT)));

        //frameworks
        List<String> frameworks = new ArrayList<>();
        valueObject.getLabels(Label.framework).forEach((key, value) -> {
            String val = value.toLowerCase(Locale.ROOT);
            String unprefixed = Label.framework.unprefixed(key);
            frameworks.add(unprefixed);
            addTextField.accept(unprefixed, val);
            genericStrings.add(unprefixed);
        });
        frameworks.forEach(s -> addTextField.accept(SearchField.LUCENE_FIELD_FRAMEWORK.getValue(), s));

        //kpis, fields are prefixed to prevent name collisions (kpis can have any names)
        statusValues.forEach(statusValue -> {
            addTextField.accept(KPI_FACET_PREFIX + statusValue.getField(), statusValue.getStatus().getName());
        });

        //frameworks name (label keys)
        addTextField.accept(SearchField.LUCENE_FIELD_GENERIC.getValue(), StringUtils.collectionToDelimitedString(genericStrings, " "));

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
                        document.add(new FacetField(field, val));
                    }
                });

        //tag facets
        Arrays.stream(valueProvider.getTags())
                .forEach(tag -> addFacetField.accept(SearchField.LUCENE_FIELD_TAG.getValue(), tag.toLowerCase(Locale.ROOT)));

        //network facets
        valueProvider.getLabels(Label.network)
                .forEach((key, value) -> addFacetField.accept(SearchField.LUCENE_FIELD_NETWORK.getValue(), value.toLowerCase(Locale.ROOT)));

        //framework facets (only key, not version)
        valueProvider.getLabels(Label.framework)
                .forEach((key, value) -> addFacetField.accept(SearchField.LUCENE_FIELD_FRAMEWORK.getValue(), Label.framework.unprefixed(key)));

        addFacetField.accept(SearchField.LUCENE_FIELD_LIFECYCLE.getValue(), valueProvider.getLabels().get(Label.lifecycle.name()));
        addFacetField.accept(SearchField.LUCENE_FIELD_CAPABILITY.getValue(), valueProvider.getLabels().get(Label.capability.name()));
        addFacetField.accept(SearchField.LUCENE_FIELD_OWNER.getValue(), valueProvider.getOwner());
        addFacetField.accept(SearchField.LUCENE_FIELD_TYPE.getValue(), valueProvider.getType());

        valueProvider.getGroup().ifPresent(s -> addFacetField.accept(SearchField.LUCENE_FIELD_GROUP.getValue(), s));
        valueProvider.getLayer().ifPresent(s -> addFacetField.accept(SearchField.LUCENE_FIELD_LAYER.getValue(), s));

        //kpis, facets are prefixed to prevent name collisions (kpis can have any names)
        statusValues.forEach(statusValue -> {
            addFacetField.accept(KPI_FACET_PREFIX + statusValue.getField(), statusValue.getStatus().getName());
        });
    }
}