package de.bonndan.nivio.search;

import de.bonndan.nivio.model.Label;
import org.springframework.lang.NonNull;

/**
 * Fixed fields in the search index.
 *
 *
 */
public enum SearchField {

    IDENTIFIER("identifier", "The local identifier of the component without parent identifier", true),

    LUCENE_FIELD_GENERIC  ("generic", "This is used to collect strings which should be directly searchable", false),
    
    PARENT_IDENTIFIER("parent", "identifier of the parent component", true),
    LUCENE_FIELD_NAME ("name", "name of the component", true),
    LUCENE_FIELD_DESCRIPTION ( "description", "description of the component", true),
    
    LUCENE_FIELD_FQI ( "fqi", "fully qualified identifier", false),
    LUCENE_FIELD_COMPONENT ( "component", "class of the component (item, group, relation...)", true),
    LUCENE_FIELD_GROUP ( "group", "parent group of an item", true),
    LUCENE_FIELD_TYPE ( "type", "type of the component", true),
    LUCENE_FIELD_OWNER ( "owner", "owner of the component", true),
    LUCENE_FIELD_TAG ( "tag", "assigned tag(s)", true),
    LUCENE_FIELD_NETWORK ( Label.network.name(), "network(s) of the component", true),
    LUCENE_FIELD_LIFECYCLE ( Label.lifecycle.name(), "network(s) of the component", true),
    //todo this is probably useless with unit/context parents
    LUCENE_FIELD_CAPABILITY ( Label.capability.name(), "capability", true),
    LUCENE_FIELD_LAYER ( "layer", "domain layer or technical layer", true),
    LUCENE_FIELD_ADDRESS ( "address", "address/url of an item", true),
    LUCENE_FIELD_FRAMEWORK ( Label.framework.name(), "frameworks of an item", true);
    
    
    private final String value;
    private final String description;
    private final boolean isPublic;

    SearchField(String value, String description, boolean isPublic) {
        this.value = value;
        this.description = description;
        this.isPublic = isPublic;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @NonNull
    public String getDescription() {
        return description;
    }
}
