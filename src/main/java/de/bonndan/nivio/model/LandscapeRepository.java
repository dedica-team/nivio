package de.bonndan.nivio.model;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LandscapeRepository {

    private final Map<String, LandscapeImpl> landscapes = new ConcurrentHashMap<>();

    public Optional<LandscapeImpl> findDistinctByIdentifier(String identifier) {
        return Optional.ofNullable(landscapes.get(identifier));
    }

    public void save(LandscapeImpl landscape) {
        landscapes.put(landscape.getIdentifier(), landscape);
    }

    public Iterable<LandscapeImpl> findAll() {
        return landscapes.values();
    }
}