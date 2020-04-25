package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.input.http.CachedResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class IconsConfiguration {

    @Bean
    public Map<String, CachedResponse> getCache() {
        return new ConcurrentHashMap<>();
    }
}
