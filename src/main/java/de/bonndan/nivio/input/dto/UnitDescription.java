package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.lang.NonNull;

import java.net.URI;

public class UnitDescription extends ComponentDescription {

    private String landscape;

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    @NonNull
    @Override
    public String getParentIdentifier() {
        return getLandscape();
    }

    @NonNull
    @Override
    public URI getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.forDescription(UnitDescription.class, null, getIdentifier(), null, null ,null, null);
    }
}
