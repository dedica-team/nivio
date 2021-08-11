package de.bonndan.nivio.output.map;

import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A service that caches map rendering.
 */
@Service
public class RenderingRepository {

    /**
     * cache map, key is FQI string representation (or debugged version)
     */
    private final Map<String, Object> renderings = new ConcurrentHashMap<>();

    /**
     * Returns an svg.
     *
     * @param landscape the landscape to render
     * @param debug     flag to enable debug messages
     * @return the svg as string
     */
    @NonNull
    public Optional<Object> get(@NonNull final String artefactType, @NonNull final Landscape landscape, boolean debug) {
        return Optional.ofNullable(renderings.get(getKey(landscape, artefactType, debug)));
    }

    /**
     * Saves a rendered artefact.
     *
     * @param artefactType type artefact
     * @param landscape    the related landscape
     * @param artefact     the rendered artefact
     * @param debug        flag
     */
    public void save(String artefactType, @NonNull final Landscape landscape, @NonNull final Object artefact, boolean debug) {
        renderings.put(getKey(landscape, artefactType, debug), artefact);
    }

    private String getKey(Landscape landscape, String type, boolean debug) {
        return Objects.requireNonNull(landscape).getFullyQualifiedIdentifier() + Objects.requireNonNull(type) + (debug ? "debug" : "");
    }
}
