package de.bonndan.nivio.input.hints;

import de.bonndan.nivio.model.RelationType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.Objects;

/**
 * A hint to new relations or items based on label values.
 *
 */
public class Hint {

    private final String targetType;
    private final RelationType relationType;
    private final String software;
    private final URI uri;
    private String target;

    /**
     * @param targetType   item.type of the target item description
     * @param relationType relation.type of the relation
     * @param software     software name, if known
     */
    public Hint(@NonNull final URI uri,
                @Nullable final String targetType,
                @Nullable final RelationType relationType,
                @Nullable final String software
    ) {
        this.uri = Objects.requireNonNull(uri);
        this.targetType = targetType;
        this.relationType = relationType;
        this.software = software;
    }

    public String getTargetType() {
        return targetType;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public String getSoftware() {
        return software;
    }

    public URI getUri() {
        return uri;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }
}
