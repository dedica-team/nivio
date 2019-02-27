package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import org.springframework.util.StringUtils;

import java.util.List;

public class SourceReferencesResolver {


    private final FileFetcher fetcher = new FileFetcher(new HttpService());

    public void resolve(final Environment env, final ProcessLog log) {

        env.getSourceReferences().forEach(ref -> {
            try {
                String source = fetcher.get(ref);
                ServiceDescriptionFactory sdf = ServiceDescriptionFormatFactory.getFactory(ref.getFormat());
                List<ServiceDescription> descriptions = sdf.fromString(source);
                if (ref.getAutoGroup() != null) {
                    descriptions.forEach(description -> {
                        if (StringUtils.isEmpty(description.getGroup()))
                            description.setGroup(ref.getAutoGroup());
                    });
                }
                env.addServices(descriptions);
            } catch (ProcessingException ex) {
                log.warn("Failed to resolve source reference " + ref, ex);
            }
        });

    }
}
