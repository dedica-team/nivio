package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.codec.digest.DigestUtils.md5;


/**
 * Resolves source references into landscape descriptions.
 */
@Service
public class SourceReferencesResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceReferencesResolver.class);

    private final InputFormatHandlerFactory formatFactory;
    private final ApplicationEventPublisher eventPublisher;

    public SourceReferencesResolver(@NonNull final InputFormatHandlerFactory formatFactory,
                                    @NonNull final ApplicationEventPublisher eventPublisher
    ) {
        this.formatFactory = formatFactory;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Resolves the config into a collection (map) of landscape DTOs.
     *
     * @param seedConfiguration the config to resolve
     * @return resulting landscape DTOs
     */
    public List<LandscapeDescription> resolve(@NonNull final SeedConfiguration seedConfiguration) {

        Map<FullyQualifiedIdentifier, LandscapeDescription> map = new HashMap<>();

        //default landscape when items are set directly
        LandscapeDescription defaultLandscapeDTO = createDefaultDTO(seedConfiguration);
        map.put(defaultLandscapeDTO.getFullyQualifiedIdentifier(), defaultLandscapeDTO);

        seedConfiguration.getSourceReferences().forEach(ref -> {
            InputFormatHandler formatHandler;
            if (ref.getAssignTemplates() != null) {
                defaultLandscapeDTO.setAssignTemplates(ref.getAssignTemplates());
            }
            try {
                formatHandler = formatFactory.getInputFormatHandler(ref);
                formatHandler.applyData(ref, defaultLandscapeDTO)
                        .forEach(landscapeDescription -> {
                            if (ref.getAssignTemplates() != null) {
                                landscapeDescription.setAssignTemplates(ref.getAssignTemplates());
                            }
                            Optional<LandscapeDescription> existing = Optional.ofNullable(map.get(landscapeDescription.getFullyQualifiedIdentifier()));
                            if (existing.isPresent()) {
                                existing.get().merge(landscapeDescription);
                            } else {
                                map.put(landscapeDescription.getFullyQualifiedIdentifier(), landscapeDescription);
                            }
                        });
            } catch (ProcessingException ex) {
                String message = ex.getMessage();
                if (ex instanceof ReadingException) {
                    message += ": " + getCauseMessage(ex.getCause());
                }
                LOGGER.error(message);
                eventPublisher.publishEvent(new ErrorEvent(seedConfiguration, ex));
                seedConfiguration.setIsPartial(true);
            } catch (RuntimeException ex) {
                String msg = String.format("Failed to resolve source reference '%s': %s", ref.getUrl(), ex.getMessage());
                LOGGER.error(msg);
                eventPublisher.publishEvent(new ErrorEvent(seedConfiguration, ex));
                seedConfiguration.setIsPartial(true);
            }

        });
        return map.values().stream()
                .peek(landscapeDescription -> landscapeDescription.setIsPartial(seedConfiguration.isPartial()))
                .collect(Collectors.toUnmodifiableList());
    }

    //keeps human-readable message, removes part starting at  [Source: (StringReader); line: 11, column: 9]
    private String getCauseMessage(Throwable cause) {
        String s = cause.getMessage().split("\\[")[0];
        if (s.endsWith("at ")) {
            s = s.substring(0, s.length() - 3);
        }

        return s.trim();
    }

    @NonNull
    private LandscapeDescription createDefaultDTO(@NonNull final SeedConfiguration seedConfiguration) {

        Objects.requireNonNull(seedConfiguration, "A seed config must be provided.");
        if (!StringUtils.hasLength(seedConfiguration.getIdentifier())) {
            throw new IllegalArgumentException("Seed config does not have an identifier to create a default landscape description.");
        }
        String identifier = StringUtils.hasLength(seedConfiguration.getIdentifier()) ?
                seedConfiguration.getIdentifier() :
                new String(md5(seedConfiguration.getSource().toString().getBytes(StandardCharsets.UTF_8)));
        LandscapeDescription landscapeDescription = new LandscapeDescription(identifier);
        landscapeDescription.setName(seedConfiguration.getName());
        landscapeDescription.setDescription(seedConfiguration.getDescription());
        landscapeDescription.setContact(seedConfiguration.getContact());
        landscapeDescription.setGroups(seedConfiguration.getGroups());
        landscapeDescription.setItems(seedConfiguration.getItems());
        if (seedConfiguration.getTemplates() != null) {
            landscapeDescription.setTemplates(seedConfiguration.getTemplates());
        }
        landscapeDescription.setConfig(seedConfiguration.getConfig());
        return landscapeDescription;

    }

}
