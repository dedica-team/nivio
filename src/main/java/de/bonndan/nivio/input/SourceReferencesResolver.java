package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


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

        Map<String, LandscapeDescription> map = new HashMap<>();

        //default landscape when items are set directly
        LandscapeDescription defaultLandscapeDTO = LandscapeDescriptionFactory.createDefaultDTO(seedConfiguration);
        map.put(defaultLandscapeDTO.getIdentifier(), defaultLandscapeDTO);

        seedConfiguration.getSourceReferences().forEach(ref -> {
            InputFormatHandler formatHandler;
            if (ref.getAssignTemplates() != null) {
                defaultLandscapeDTO.setAssignTemplates(ref.getAssignTemplates());
            }
            try {
                formatHandler = formatFactory.getInputFormatHandler(ref);
                formatHandler.applyData(ref, defaultLandscapeDTO).forEach(dto -> handleDTO(map, ref, dto));
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

    /**
     * Ensures that the produced landscape description is merged or updated into the map
     */
    private void handleDTO(Map<String, LandscapeDescription> map, SourceReference ref, LandscapeDescription landscapeDescription) {
        if (ref.getAssignTemplates() != null) {
            landscapeDescription.setAssignTemplates(ref.getAssignTemplates());
        }
        Optional<LandscapeDescription> existing = Optional.ofNullable(map.get(landscapeDescription.getIdentifier()));
        if (existing.isPresent()) {
            LandscapeDescription landscapeDescription1 = existing.get();
            String otherFQI = Objects.requireNonNull(landscapeDescription).getIdentifier();
            if (!existing.get().getIdentifier().equals(otherFQI)) {
                throw new IllegalArgumentException(String.format("Other landscape description has different fqi %s", otherFQI));
            }

            landscapeDescription1.mergeUnits(landscapeDescription.getIndexReadAccess().all(UnitDescription.class));
            landscapeDescription1.mergeContexts(landscapeDescription.getIndexReadAccess().all(ContextDescription.class));
            landscapeDescription1.mergeGroups(landscapeDescription.getIndexReadAccess().all(GroupDescription.class));
            landscapeDescription1.mergeItems(landscapeDescription.getIndexReadAccess().all(ItemDescription.class));
        } else {
            map.put(landscapeDescription.getIdentifier(), landscapeDescription);
        }
    }

    //keeps human-readable message, removes part starting at  [Source: (StringReader); line: 11, column: 9]
    private String getCauseMessage(Throwable cause) {
        String s = cause.getMessage().split("\\[")[0];
        if (s.endsWith("at ")) {
            s = s.substring(0, s.length() - 3);
        }

        return s.trim();
    }

}
