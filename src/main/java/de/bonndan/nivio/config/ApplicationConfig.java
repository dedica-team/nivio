package de.bonndan.nivio.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import de.bonndan.nivio.input.Seed;
import de.bonndan.nivio.output.icons.LocalIcons;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;

import static de.bonndan.nivio.output.icons.LocalIcons.DEFAULT_ICONS_FOLDER;

@Configuration
@EnableConfigurationProperties(SeedProperties.class)
public class ApplicationConfig {

    private final SeedProperties seedProperties;

    public ApplicationConfig(SeedProperties seedProperties) {
        this.seedProperties = seedProperties;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Seed seed() {
        return new Seed(seedProperties.getSeed(), seedProperties.getDemo());
    }

    @Bean
    public LocalIcons getLocalIcons(@Value("${nivio.iconFolder:" + DEFAULT_ICONS_FOLDER + "}") String iconsFolder) {
        return new LocalIcons(iconsFolder);
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.INSTANCE);
        return new Jackson2ObjectMapperBuilder()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modulesToInstall(module);
    }

}
