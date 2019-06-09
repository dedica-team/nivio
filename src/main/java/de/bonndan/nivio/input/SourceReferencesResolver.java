package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.List;

import static de.bonndan.nivio.landscape.ServiceItems.find;

public class SourceReferencesResolver {


    private final FileFetcher fetcher = new FileFetcher(new HttpService());

    public void resolve(final Environment env, final ProcessLog log) {

        URL baseUrl = URLHelper.getParentPath(env.getSource());

        env.getSourceReferences().forEach(ref -> {
            try {
                String source = fetcher.get(ref, baseUrl);
                ServiceDescriptionFactory factory = ServiceDescriptionFormatFactory.getFactory(ref.getFormat());
                List<ServiceDescription> descriptions = factory.fromString(source);

                ref.getAssignTemplates().entrySet().forEach(templateAssignments -> {

                    ServiceItem template = find(templateAssignments.getKey(), "", env.getTemplates());
                    if (template == null) {
                        log.warn("Could not find template to assign: " + templateAssignments.getKey());
                        return;
                    }

                    templateAssignments.getValue().forEach(identifier -> {
                        if ("*".equals(identifier)) {
                            descriptions.forEach(item -> ServiceDescriptionFactory.assignTemplateValues(item, (ServiceDescription)template));

                        } else {
                            ServiceItem item = find(identifier, "", descriptions);
                            if (item == null) {
                                log.warn("Could not assign template " + template.getIdentifier() + ", service " + identifier + " not found.");
                                return;
                            }
                            ServiceDescriptionFactory.assignTemplateValues((ServiceDescription)item, (ServiceDescription)template);
                        }

                    });

                });

                env.addServices(descriptions);
            } catch (ProcessingException ex) {
                log.warn("Failed to resolve source reference " + ref, ex);
                env.setIsPartial(true);
            }
        });

    }
}
