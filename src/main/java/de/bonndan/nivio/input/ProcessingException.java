package de.bonndan.nivio.input;

import com.fasterxml.jackson.annotation.JsonValue;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.springframework.lang.Nullable;

/**
 * Generic exception tied to a {@link LandscapeDescription} or {@link SourceReference}.
 *
 *
 *
 */
public class ProcessingException extends RuntimeException {

    private final LandscapeDescription landscapeDescription;
    private final SourceReference sourceReference;

    public ProcessingException(final LandscapeDescription landscapeDescription, final String message) {
        super(message);
        this.landscapeDescription = landscapeDescription;
        this.sourceReference = null;
    }

    public ProcessingException(String message, Throwable throwable) {
        super(message, throwable);
        this.landscapeDescription = null;
        this.sourceReference = null;
    }

    public ProcessingException(final LandscapeDescription landscape, final String message, Throwable throwable) {
        super(message, throwable);
        this.landscapeDescription = landscape;
        this.sourceReference = null;
    }

    public ProcessingException(final SourceReference sourceReference, final String message) {
        super(message);
        this.sourceReference = sourceReference;
        this.landscapeDescription = null;
    }

    public ProcessingException(final SourceReference reference, final String message, Exception e) {
        super(message,e);
        this.sourceReference = reference;
        this.landscapeDescription = null;
    }

    @Nullable
    public LandscapeDescription getLandscapeDescription() {
        return landscapeDescription;
    }

    @JsonValue
    public String getMessage() {
        return super.getMessage();
    }

    @Nullable
    public SourceReference getSourceReference() {
        return sourceReference;
    }
}
