package de.bonndan.nivio.output;

import de.bonndan.nivio.assessment.Assessment;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class RendererOptions {

    private final Assessment assessment;
    private final List<URI> processes;
    private final boolean debug;

    public RendererOptions(Assessment assessment, List<URI> processes, boolean debug) {
        this.assessment = assessment;
        this.processes = processes;
        this.debug = debug;
    }

    public RendererOptions(Assessment assessment, boolean debug) {
        this(assessment, Collections.emptyList(), debug);
    }

    @NonNull
    public Assessment getAssessment() {
        return assessment == null ? Assessment.empty() : assessment;
    }

    public boolean isDebug() {
        return debug;
    }
}
