package de.bonndan.nivio.input.dto;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class LandscapeSource {

    public final URL url;
    public final String yaml;

    public LandscapeSource(@NonNull final URL url) {
        this.url = Objects.requireNonNull(url);
        this.yaml = null;
    }

    public LandscapeSource(@Nullable final String staticSource) {
        this.url = null;
        this.yaml = staticSource;
    }

    /**
     * @return the external source URL
     */
    @NonNull
    public Optional<URL> getURL() {
        return Optional.ofNullable(url);
    }

    /**
     * @return the external source URL
     */
    @Nullable
    public String getStaticSource() {
        return yaml;
    }
}
