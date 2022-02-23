package de.bonndan.nivio.model;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LandscapeRepository {

    private final Map<String, Landscape> landscapes = new ConcurrentHashMap<>();

    public Optional<Landscape> findDistinctByIdentifier(@NonNull final String identifier) {
        return Optional.ofNullable(landscapes.get(Objects.requireNonNull(identifier).toLowerCase(Locale.ROOT)));
    }

    public void save(@NonNull final Landscape landscape) {
        landscapes.put(landscape.getIdentifier().toLowerCase(Locale.ROOT), landscape);
    }

    public Iterable<Landscape> findAll() {
        return landscapes.values();
    }
}