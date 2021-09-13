package de.bonndan.nivio.input.dto;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * The origin of a {@link de.bonndan.nivio.input.SeedConfiguration} which can either be a URL or textual content.
 *
 *
 */
public class Source {

    public final URL url;
    public final String yaml;

    public Source(@NonNull final URL url) {
        this.url = Objects.requireNonNull(url);
        this.yaml = null;
    }

    public Source(@Nullable final String staticSource) {
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

    public String get() {
        if (url != null)
            return url.toString();

        return yaml;
    }
}
