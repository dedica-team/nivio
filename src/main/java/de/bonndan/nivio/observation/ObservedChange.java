package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.model.Landscape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result containing changes sources and errors while scanning.
 */
public class ObservedChange {

    private final List<ProcessingException> errors = new ArrayList<>();
    private List<String> changes;
    private boolean error;

    public ObservedChange(final String change) {
        this.changes = List.of(change);
    }

    public void setChanges(List<String> changes) {
        this.changes = changes;
    }

    public void setHasError() {
        this.error = true;
    }

    public boolean hasError() {
        return error;
    }

    public void addError(ProcessingException e) {
        this.errors.add(e);
    }

    public List<ProcessingException> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public List<String> getChanges() {
        return changes;
    }

}
