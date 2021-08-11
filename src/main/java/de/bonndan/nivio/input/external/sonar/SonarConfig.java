package de.bonndan.nivio.input.external.sonar;

import org.sonar.wsclient.SonarClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@EnableConfigurationProperties(SonarProperties.class)
public class SonarConfig {

    private final SonarProperties sonarProperties;

    public SonarConfig(SonarProperties sonarProperties) {
        this.sonarProperties = sonarProperties;
    }

    @Bean
    public SonarClient.Builder getSonarClientBuilder() {

        Optional<String> serverUrl = Optional.ofNullable(sonarProperties.getServerUrl());

        if (serverUrl.isEmpty()) {
            return null;
        }
        SonarClient.Builder builder = SonarClient.builder().url(serverUrl.get());


       Optional.ofNullable(sonarProperties.getLogin()) .ifPresent(builder::login);

        Optional.ofNullable(sonarProperties.getPassword()).ifPresent(builder::password);
        Optional.ofNullable(sonarProperties.getProxyHost()).ifPresent(
                host -> Optional.ofNullable(sonarProperties.getProxyPort())
                        .ifPresent(port -> builder.proxy(host, Integer.parseInt(port)))
        );

        return builder;
    }

}
