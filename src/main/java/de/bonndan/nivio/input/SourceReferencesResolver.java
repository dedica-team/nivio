package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.List;

public class SourceReferencesResolver {


    private final FileFetcher fetcher = new FileFetcher(new HttpService());

    public void resolve(final Environment env, final ProcessLog log) {

        URL baseUrl = URLHelper.getParentPath(env.getSource());

        env.getSourceReferences().forEach(ref -> {
            try {
                String source = fetcher.get(ref, baseUrl);
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
