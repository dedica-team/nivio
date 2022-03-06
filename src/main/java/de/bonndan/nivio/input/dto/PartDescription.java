package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.lang.NonNull;

import java.net.URI;

/**
 * Subcomponents of {@link ItemDescription}
 */
public class PartDescription extends ComponentDescription {

    private String item;

    public PartDescription() {

    }

    public PartDescription(String identifier) {
        super();
        this.setIdentifier(identifier);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartDescription)) return false;

        return getIdentifier().equals(((PartDescription) o).getIdentifier());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
