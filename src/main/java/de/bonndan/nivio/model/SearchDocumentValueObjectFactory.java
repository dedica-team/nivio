package de.bonndan.nivio.model;

import de.bonndan.nivio.search.SearchDocumentValueObject;

import java.util.Locale;

public class SearchDocumentValueObjectFactory {

    private SearchDocumentValueObjectFactory() {}

    public static SearchDocumentValueObject createFor(Component component) {
        return createFor(component, null);
    }

    public static SearchDocumentValueObject createForRelation(Relation relation) {
        return createFor(relation, null);
    }

    public static SearchDocumentValueObject createForItem(Item component) {

        return new SearchDocumentValueObject(
                component.getFullyQualifiedIdentifier(),
                component.getIdentifier(),
                component.getParent().getIdentifier(),
                component.getClass().getSimpleName().toLowerCase(Locale.ROOT),
                component.getName(),
                component.getDescription(),
                component.getOwner(),
                component.getTags(),
                component.getType(),
                component.getLinks(),
                component.getLabels(),
                component.getLayer(),
                component.getParent().getIdentifier(),
                component.getAddress()
        );
    }

    static <T extends Component> SearchDocumentValueObject createFor(T component, String parentIdentifier) {
        return new SearchDocumentValueObject(
                component.getFullyQualifiedIdentifier(),
                component.getIdentifier(),
                parentIdentifier,
                component.getClass().getSimpleName().toLowerCase(Locale.ROOT),
                component.getName(),
                component.getDescription(),
                component.getOwner(),
                component.getTags(),
                component.getType(),
                component.getLinks(),
                component.getLabels()
        );
    }
}
