package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.DataFlowDescription;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.DataFlow;
import de.bonndan.nivio.landscape.DataFlowItem;
import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.landscape.ServiceItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.bonndan.nivio.landscape.ServiceItems.find;

public class SourceReferencesResolver {

    public void resolve(final Environment env, final ProcessLog log) {

        env.getSourceReferences().forEach(ref -> {
            try {
                ServiceDescriptionFactory factory = ServiceDescriptionFormatFactory.getFactory(ref, env);

                List<ServiceDescription> descriptions = factory.getDescriptions(ref);

                ref.getAssignTemplates().entrySet().forEach(templateAssignments -> {

                    ServiceItem template = find(templateAssignments.getKey(), "", env.getTemplates()).orElse(null);
                    if (template == null) {
                        log.warn("Could not find template to assign: " + templateAssignments.getKey());
                        return;
                    }

                    templateAssignments.getValue().forEach(identifier -> {
                        ServiceItems.filter(identifier, descriptions)
                                .forEach(item -> ServiceDescriptionFactory.assignTemplateValues((ServiceDescription) item, (ServiceDescription) template));
                    });

                });

                env.addServices(descriptions);
            } catch (ProcessingException ex) {
                log.warn("Failed to resolve source reference " + ref, ex);
                env.setIsPartial(true);
            }
        });

        resolveTemplateQueries(env.getServiceDescriptions());
    }

    /**
     * Finds providers or data flow targets named in queries.
     */
    private void resolveTemplateQueries(final List<ServiceDescription> serviceDescriptions) {
        serviceDescriptions.forEach(serviceDescription -> {

            //provider
            List<String> provided_by = serviceDescription.getProvided_by();
            serviceDescription.setProvided_by(new ArrayList<>());
            provided_by.forEach(condition -> {
                ServiceItems.filter(condition, serviceDescriptions)
                        .forEach(result -> serviceDescription.getProvided_by().add(result.getIdentifier()));
            });

            serviceDescription.getDataFlow().forEach(dataFlowItem -> {
                ServiceItems.filter(dataFlowItem.getTarget(), serviceDescriptions).stream()
                        .findFirst()
                        .ifPresent(service -> ((DataFlowDescription)dataFlowItem).setTarget(service.getFullyQualifiedIdentifier().toString()));
            });
        });
    }

}
