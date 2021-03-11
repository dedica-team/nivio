package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.net.URL;
import java.util.Objects;


/**
 * Resolves source references into collections of item descriptions.
 */
public class SourceReferencesResolver {

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

        URL baseUrl = URLHelper.getParentPath(landscapeDescription.getSource()).orElse(null);
        landscapeDescription.getSourceReferences().forEach(ref -> {
            InputFormatHandler factory;
            try {
                factory = formatFactory.getInputFormatHandler(ref);
            } catch (ProcessingException ex) {
                log.error(ex.getMessage());
                eventPublisher.publishEvent(new ProcessingErrorEvent(landscapeDescription.getFullyQualifiedIdentifier(), ex));
                landscapeDescription.setIsPartial(true);
                return;
            } catch (RuntimeException ex) {
                String msg = "Failed to resolve source reference '" + ref.getUrl() + "': " + ex.getMessage();
                log.warn(msg);
                landscapeDescription.setIsPartial(true);
                return;
            }

            try {
                factory.applyData(ref, baseUrl, landscapeDescription);
            } catch (ProcessingException ex) {
                String message = ex.getMessage();
                if (ex instanceof ReadingException) {
                    message += ": " + getCauseMessage(ex.getCause());
                }
                log.error(message);
                eventPublisher.publishEvent(new ProcessingErrorEvent(landscapeDescription.getFullyQualifiedIdentifier(), ex));
                landscapeDescription.setIsPartial(true);
            } catch (RuntimeException ex) {
                log.warn(ex.getMessage());
                landscapeDescription.setIsPartial(true);
            }
        });
    }

    //keeps human readable message, removes part starting at  [Source: (StringReader); line: 11, column: 9]
    private String getCauseMessage(Throwable cause) {
        String s = cause.getMessage().split("\\[")[0];
        if (s.endsWith("at ")) {
            s = s.substring(0, s.length()-3);
        }

        return s.trim();
    }

}
