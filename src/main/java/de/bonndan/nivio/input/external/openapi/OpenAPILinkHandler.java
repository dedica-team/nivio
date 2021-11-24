package de.bonndan.nivio.input.external.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.external.ExternalLinkHandler;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.Link;
import io.swagger.v3.oas.integration.IntegrationObjectMapperFactory;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Reads swagger / openAPI specs.
 *
 * See https://swagger.io/specification/
 */
public class OpenAPILinkHandler implements ExternalLinkHandler {

    public static final String NAMESPACE = "openapi";

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAPILinkHandler.class);

    private final HttpService httpService;
    private final ObjectMapper mapper;

    public OpenAPILinkHandler(HttpService httpService) {
        this.httpService = httpService;
        mapper = IntegrationObjectMapperFactory.createJson();
    }

    @Override
    public CompletableFuture<ComponentDescription> resolve(Link link) {
        LOGGER.info("Resolving OpenAPI link {}", link.getHref());

        try {
            String s = httpService.get(link.getHref());
            return CompletableFuture.completedFuture(from(mapper.readValue(s, OpenAPI.class)));
        } catch (Exception e) {
            LOGGER.error("Failed", e);
            return CompletableFuture.failedFuture(e);
        }

    }

    private ItemDescription from(OpenAPI openAPI) {
        ItemDescription desc = new ItemDescription();

        //external doc to link
        Optional.ofNullable(openAPI.getExternalDocs()).ifPresent(externalDocumentation -> {
            try {
                Link link1 = new Link(new URL(externalDocumentation.getUrl()));
                link1.setProperty("description", externalDocumentation.getDescription());
                desc.getLinks().put(NAMESPACE + "_externaldoc", link1);
            } catch (Exception e) {
                LOGGER.warn(String.format("Failed to process externaldocs: %s", e.getMessage()));
            }
        });

        //paths to interfaces
        openAPI.getPaths().forEach((s, pathItem) -> {
            asInterfaceDescription(s, pathItem.getGet(), "GET")
                    .ifPresent(iface -> desc.getInterfaces().add(iface));
            asInterfaceDescription(s, pathItem.getPost(), "POST")
                    .ifPresent(iface -> desc.getInterfaces().add(iface));
            asInterfaceDescription(s, pathItem.getPut(), "PUT")
                    .ifPresent(iface -> desc.getInterfaces().add(iface));
            asInterfaceDescription(s, pathItem.getDelete(), "DELETE")
                    .ifPresent(iface -> desc.getInterfaces().add(iface));
            asInterfaceDescription(s, pathItem.getOptions(), "OPTIONS")
                    .ifPresent(iface -> desc.getInterfaces().add(iface));
            asInterfaceDescription(s, pathItem.getHead(), "HEAD")
                    .ifPresent(iface -> desc.getInterfaces().add(iface));
        });

        //common info
        desc.setDescription(openAPI.getInfo().getDescription());
        if (openAPI.getInfo().getContact() != null) {
            desc.setContact(openAPI.getInfo().getContact().getEmail());
            if (!StringUtils.hasLength(desc.getContact())) {
                desc.setContact(openAPI.getInfo().getContact().getName());
            }
        }
        desc.setLabel(NAMESPACE + "_version", openAPI.getInfo().getVersion());
        if (openAPI.getInfo().getLicense() != null) {
            desc.setLabel(NAMESPACE + "_license", openAPI.getInfo().getLicense().getName());
        }
        desc.setLabel(NAMESPACE + "_terms", openAPI.getInfo().getTermsOfService());
        desc.setLabel(NAMESPACE + "_title", openAPI.getInfo().getTitle());
        if (openAPI.getTags() != null) {
            desc.setLabel(NAMESPACE + "_tags", openAPI.getTags().stream().map(Tag::getName).collect(Collectors.joining(", ")));
        }

        return desc;
    }

    private Optional<InterfaceDescription> asInterfaceDescription(String path, @Nullable Operation op, String method) {
        if (op == null) {
            return Optional.empty();
        }

        InterfaceDescription iface = new InterfaceDescription();
        iface.setName(String.format("%s %s", method, path));
        iface.setDeprecated(op.getDeprecated());
        iface.setDescription(op.getDescription());
        iface.setPath(path);
        if (op.getParameters() != null) {
            iface.setParameters(op.getParameters().stream()
                    .filter(Objects::nonNull)
                    .map(Parameter::getName)
                    .collect(Collectors.joining(", ")));
        }
        if (op.getRequestBody() != null && op.getRequestBody().get$ref() != null) {
            iface.setPayload(op.getRequestBody().get$ref());
        }
        if (op.getSecurity() != null) {
            iface.setProtection(
                    op.getSecurity().stream()
                            .map(req -> String.join(", ", req.keySet())).collect(Collectors.joining(" / "))
            );
        }
        iface.setSummary(op.getSummary());

        return Optional.of(iface);
    }
}
