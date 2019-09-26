package de.bonndan.nivio.landscape;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LandscapeRepository {

    private final Map<String, Landscape> landscapes = new ConcurrentHashMap<>();

    public Optional<Landscape> findDistinctByIdentifier(String identifier) {
        return Optional.ofNullable(landscapes.get(identifier));
    }

    public void save(Landscape landscape) {
        landscapes.put(landscape.getIdentifier(), landscape);
    }

    public Iterable<Landscape> findAll() {
        return landscapes.values();
    }
}