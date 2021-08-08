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
    public <T> Optional<T> get(Class<T> artefactType, @NonNull final Landscape landscape, boolean debug) {
        Object o = renderings.get(getKey(landscape, artefactType.getSimpleName(), debug));
        return (Optional<T>) Optional.ofNullable(o);
    }

    private String getKey(Landscape landscape, String type, boolean debug) {
        return Objects.requireNonNull(landscape).getFullyQualifiedIdentifier() + type + (debug ? "debug" : "");
    }

    /**
     * Saves a rendered artefact.
     *
     * @param artefactType classname of the artefact impl
     * @param landscape    the related landscape
     * @param artefact     the rendered artefact
     * @param debug        flag
     */
    public void save(Class<?> artefactType, @NonNull final Landscape landscape, @NonNull final Object artefact, boolean debug) {
        renderings.put(getKey(landscape, artefactType.getSimpleName(), debug), artefact);
    }
}
