package de.bonndan.nivio.input.external.springboot;

import de.bonndan.nivio.assessment.kpi.HealthKPI;
import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.external.ExternalLinkHandler;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Reads Spring Boot health actuator output and stores it in labels.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/#health
 */
public class SpringBootHealthHandler implements ExternalLinkHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(SpringBootHealthHandler.class);

    private final RestTemplate restTemplate;

    public SpringBootHealthHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public CompletableFuture<ComponentDescription> resolve(Link link) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<JsonCompositeHealth> exchange = restTemplate.exchange(link.getHref().toURI(), HttpMethod.GET, entity, JsonCompositeHealth.class);

            if (exchange.getStatusCode().equals(HttpStatus.OK)) {
                return CompletableFuture.completedFuture(toData(exchange.getBody()));
            }
            String msg = String.format("Got status code %s while trying to resolve %s", exchange.getStatusCode(), link.getHref());
            LOGGER.warn(msg);
            return CompletableFuture.failedFuture(new RuntimeException(msg));
        } catch (URISyntaxException | HttpServerErrorException | ResourceAccessException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private ItemDescription toData(@Nullable JsonCompositeHealth body) {
        ItemDescription itemDescription = new ItemDescription();
        if (body == null) {
            return itemDescription;
        }

        Map<String, String> labels = new HashMap<>();
        labels.put(Label.health.toString(), asHealth(body.getStatus()));
        body.getComponents().forEach((s, healthComponent) -> applyEntry(labels, s, healthComponent));

        itemDescription.getLabels().putAll(labels);
        return itemDescription;
    }

    private String asHealth(String status) {
        if (status.equals(Status.UP.toString())) {
            return HealthKPI.HEALTHY;
        }

        if (status.equals(Status.UNKNOWN.toString())) {
            return "";
        }

        return HealthKPI.UNHEALTHY;
    }

    private void applyEntry(final Map<String, String> labels, final String key, final JsonCompositeHealth value) {
        if (value == null) {
            return;
        }

        if (value.getComponents() != null) {
            value.getComponents().forEach((s, healthComponent) -> applyEntry(labels, s, healthComponent));
        }

        if (value.getStatus() != null) {
            labels.put(Label.health.withPrefix(key), asHealth(value.getStatus()));
        }

        if (value.getDetails() != null) {
            value.getDetails().forEach((s, detail) -> labels.put(Label.withPrefix(Label.health, key, s), detail));
        }
    }
}
