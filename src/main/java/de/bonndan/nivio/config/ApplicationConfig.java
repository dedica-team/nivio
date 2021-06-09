package de.bonndan.nivio.config;

import de.bonndan.nivio.input.Seed;
import de.bonndan.nivio.output.icons.LocalIcons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static de.bonndan.nivio.output.icons.LocalIcons.DEFAULT_ICONS_FOLDER;

@Configuration
@EnableConfigurationProperties(SeedProperties.class)
public class ApplicationConfig {

    private final SeedProperties seedProperties;

    @Autowired
    public ApplicationConfig(SeedProperties seedProperties) {
        this.seedProperties = seedProperties;
    }

    @Bean
    public WebMvcConfigurer configurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Seed seed() {
        return new Seed(ConfigurableEnvVars.SEED.value());
    }

    @Bean
    public LocalIcons getLocalIcons(@Value("${nivio.iconFolder:" + DEFAULT_ICONS_FOLDER + "}") String iconsFolder) {
        return new LocalIcons(iconsFolder);
    }

}
