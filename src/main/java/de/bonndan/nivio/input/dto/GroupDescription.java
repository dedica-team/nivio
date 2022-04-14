package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;

import java.net.URI;

@Schema(description = "A group of items. Could be used as bounded context, for instance.")
public class GroupDescription extends ComponentDescription {

    @Schema(description = "The parent identifier", example = "shipping")
    private String context;

    public GroupDescription() {

    }

    public GroupDescription(String identifier) {
        this.setIdentifier(identifier);
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void assignNotNull(GroupDescription increment) {
        super.assignNotNull(increment);
        if (increment.getContext() != null)
            setContext(increment.getContext());
    }

    @NonNull
    @Override
    public URI getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.forDescription(GroupDescription.class, null,null, getParentIdentifier(), getIdentifier(), null, null);
    }

    @NonNull
    @Override
    public String getParentIdentifier() {
        return context;
    }
}
