package de.bonndan.nivio;

import de.bonndan.nivio.input.Seed;
import de.bonndan.nivio.output.icons.LocalIcons;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static de.bonndan.nivio.output.icons.LocalIcons.DEFAULT_ICONS_FOLDER;

@SpringBootApplication
@EnableScheduling
public class Application {

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
        return new Seed();
    }

    @Bean
    public LocalIcons getLocalIcons(@Value("${nivio.iconFolder:" + DEFAULT_ICONS_FOLDER + "}") String iconsFolder) {
        return new LocalIcons(iconsFolder);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
