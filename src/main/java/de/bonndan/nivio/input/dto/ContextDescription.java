package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;

import java.net.URI;

public class ContextDescription extends ComponentDescription {

    @Schema(description = "The parent identifier", example = "shipping")
    private String unit;

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void assignNotNull(ContextDescription increment) {
        super.assignNotNull(increment);
        if (increment.getUnit() != null) {
            setUnit(increment.getUnit());
        }
    }

    @NonNull
    @Override
    public String getParentIdentifier() {
        return getUnit();
    }

    @NonNull
    @Override
    public URI getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.forDescription(ContextDescription.class, null, unit, getIdentifier(), null, null, null);
    }
}
