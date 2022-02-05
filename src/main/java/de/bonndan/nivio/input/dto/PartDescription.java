package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.lang.NonNull;

import java.net.URI;

/**
 * Subcomponents of {@link ItemDescription}
 */
public class PartDescription extends ComponentDescription {

    private String item;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @NonNull
    @Override
    public URI getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.forDescription(PartDescription.class, null, null, null, null, item, getIdentifier());
    }


    @Override
    public String getParentIdentifier() {
        return getItem();
    }
}
