package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.Environment;

public class SourceReferencesResolver {


    private final FileFetcher fetcher = new FileFetcher(new HttpService());

    public void resolve(final Environment env, final ProcessLog log) {

        env.getSourceReferences().forEach(ref -> {
            try {
                String source = fetcher.get(ref);
                ServiceDescriptionFactory sdf = ServiceDescriptionFormatFactory.getFactory(ref.getFormat());
                env.addServices(sdf.fromString(source));
            } catch (ProcessingException ex) {
                log.warn("Failed to resolve source reference " + ref, ex);
            }
        });

    }
}
