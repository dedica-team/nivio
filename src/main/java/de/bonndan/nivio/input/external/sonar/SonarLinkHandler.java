package de.bonndan.nivio.input.external.sonar;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.external.ExternalLinkHandler;
import de.bonndan.nivio.model.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.SonarClient;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SonarLinkHandler implements ExternalLinkHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(SonarLinkHandler.class);

    private final Optional<SonarClient> sonarClient;
    private final String sonarApiEndpoint;

    public SonarLinkHandler(Optional<SonarClient> sonarClient,String sonarApiEndpoint) {
        this.sonarClient = sonarClient;

        this.sonarApiEndpoint = sonarApiEndpoint;
    }

    /**
     * https://medium.com/@shanchathusanda/https-medium-com-shanchathusanda-integrating-sonarqube-apis-with-a-java-application-de8fdc8b951f
     * @param link
     * @return
     */
    @Override
    public CompletableFuture<ComponentDescription> resolve(Link link) {
        if (sonarClient.isEmpty()) {
            return CompletableFuture.failedFuture(new RuntimeException("SonarClient is not configured"));
        }

        SonarClient sonarClient = this.sonarClient.get();
        return CompletableFuture.completedFuture(new ItemDescription());
    }
}
