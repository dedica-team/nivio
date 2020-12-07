package de.bonndan.nivio.input.linked;

import org.sonar.wsclient.SonarClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class SonarConfig {

    public static final String SONAR_LOGIN = "SONAR_LOGIN";
    public static final String SONAR_SERVER_URL = "SONAR_SERVER_URL";
    public static final String SONAR_PASSWORD = "SONAR_PASSWORD";
    public static final String SONAR_PROXY_HOST = "SONAR_PROXY_HOST";
    public static final String SONAR_PROXY_PORT = "SONAR_PROXY_PORT";

    @Bean
    public SonarClient.Builder getSonarClientBuilder() {
        SonarClient.Builder builder = SonarClient.builder().url(System.getenv().get(SONAR_SERVER_URL));

        getEnv(SONAR_LOGIN).ifPresent(builder::login);
        getEnv(SONAR_PASSWORD).ifPresent(builder::password);
        getEnv(SONAR_PROXY_HOST).ifPresent(
                host -> getEnv(SONAR_PROXY_PORT)
                        .ifPresent(port -> builder.proxy(host, Integer.parseInt(port)))
        );

        return builder;
    }

    private Optional<String> getEnv(String key) {
        return Optional.ofNullable(System.getenv().get(key));
    }
}
