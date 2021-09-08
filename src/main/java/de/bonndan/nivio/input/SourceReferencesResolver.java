package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeSource;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.net.URL;
import java.util.Objects;


/**
 * Resolves source references into collections of item descriptions.
 */
public class SourceReferencesResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceReferencesResolver.class);

    private final InputFormatHandlerFactory formatFactory;
    private final ProcessLog log;
    private final ApplicationEventPublisher eventPublisher;

    public SourceReferencesResolver(@NonNull final InputFormatHandlerFactory formatFactory,
                                    @NonNull final ProcessLog logger,
                                    @NonNull final ApplicationEventPublisher eventPublisher
    ) {
        this.formatFactory = Objects.requireNonNull(formatFactory);
        this.log = Objects.requireNonNull(logger);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void resolve(final LandscapeDescription landscapeDescription) {

        final var baseUrl = getBaseUrl(landscapeDescription.getSource());

        landscapeDescription.getSourceReferences().forEach(ref -> {
            InputFormatHandler formatHandler;
            try {
                formatHandler = formatFactory.getInputFormatHandler(ref);
            } catch (ProcessingException ex) {
                log.error(ex.getMessage());
                eventPublisher.publishEvent(new ProcessingErrorEvent(landscapeDescription.getFullyQualifiedIdentifier(), ex));
                landscapeDescription.setIsPartial(true);
                return;
            } catch (RuntimeException ex) {
                String msg = String.format("Failed to resolve source reference '%s': %s", ref.getUrl(), ex.getMessage());
                log.warn(msg);
                landscapeDescription.setIsPartial(true);
                return;
            }

            try {
                formatHandler.applyData(ref, baseUrl, landscapeDescription);
            } catch (ProcessingException ex) {
                String message = ex.getMessage();
                if (ex instanceof ReadingException) {
                    message += ": " + getCauseMessage(ex.getCause());
                }
                log.error(message);
                eventPublisher.publishEvent(new ProcessingErrorEvent(landscapeDescription.getFullyQualifiedIdentifier(), ex));
                landscapeDescription.setIsPartial(true);
            } catch (RuntimeException ex) {
                LOGGER.warn("Could not resolve source reference {}: {}", ref, ex.getMessage(), ex);
                log.warn(String.format("Failed to resolve source reference %s properly.", ref.getUrl()));
                landscapeDescription.setIsPartial(true);
            }
        });
    }

    private URL getBaseUrl(LandscapeSource source) {
        if (source != null) {
            return source.getURL().flatMap(URLHelper::getParentPath).orElse(null);
        }
        return null;
    }

    //keeps human readable message, removes part starting at  [Source: (StringReader); line: 11, column: 9]
    private String getCauseMessage(Throwable cause) {
        String s = cause.getMessage().split("\\[")[0];
        if (s.endsWith("at ")) {
            s = s.substring(0, s.length() - 3);
        }

        return s.trim();
    }

}
