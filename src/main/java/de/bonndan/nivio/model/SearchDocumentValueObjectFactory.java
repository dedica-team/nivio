package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.search.SearchDocumentValueObject;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * Creates {@link SearchDocumentValueObject}s for various {@link Component}s.
 */
public class SearchDocumentValueObjectFactory {

    private SearchDocumentValueObjectFactory() {
    }

    /**
     * Factory method to create a value object
     *
     * @param component any component
     * @return value object or null
     */
    @Nullable
    public static SearchDocumentValueObject createFor(@NonNull final Component component) {
        Objects.requireNonNull(component, "Component to be indexed is null");

        if (component instanceof Relation && RelationType.CHILD.name().equals(component.getType())) {
            return null;
        }

        return new SearchDocumentValueObject(
                component.getFullyQualifiedIdentifier(),
                component.getIdentifier(),
                component.getParentIdentifier(),
                ComponentClass.valueOf(component.getClass()),
                component.getName(),
                component.getDescription(),
                component.getOwner(),
                component.getTags(),
                component.getType(),
                component.getLinks(),
                component.getLabels(),
                getLayer(component),
                getAddress(component)
        );
    }

    private static String getLayer(Component component) {
        if (component instanceof Item)
            return ((Item) component).getLayer();
        if (component instanceof ItemDescription)
            return ((ItemDescription) component).getLayer();

        return null;
    }

    private static String getAddress(Component component) {
        if (component instanceof Item)
            return ((Item) component).getAddress();
        if (component instanceof ItemDescription)
            return ((ItemDescription) component).getAddress();

        return null;
    }
}
