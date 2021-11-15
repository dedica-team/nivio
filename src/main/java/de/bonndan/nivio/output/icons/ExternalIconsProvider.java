package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.util.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "iconurls")
@PropertySource(value = "classpath:iconurls.yml", factory = YamlPropertySourceFactory.class)
public class ExternalIconsProvider {
    private Map<String, String> urls;

    public Map<String, String> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }
}

