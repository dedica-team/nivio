package de.bonndan.nivio.input.external.sonar;

import org.sonar.wsclient.SonarClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static de.bonndan.nivio.config.ConfigurableEnvVars.*;

@Configuration
public class SonarConfig {

    @Bean
    public SonarClient.Builder getSonarClientBuilder() {

        Optional<String> serverUrl = SONAR_SERVER_URL.value();
        if (serverUrl.isEmpty()) {
            return null;
        }
        SonarClient.Builder builder = SonarClient.builder().url(serverUrl.get());

        SONAR_LOGIN.value().ifPresent(builder::login);
        SONAR_PASSWORD.value().ifPresent(builder::password);
        SONAR_PROXY_HOST.value().ifPresent(
                host -> SONAR_PROXY_PORT.value()
                        .ifPresent(port -> builder.proxy(host, Integer.parseInt(port)))
        );

        return builder;
    }

}
