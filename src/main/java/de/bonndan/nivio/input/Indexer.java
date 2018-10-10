package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.*;
import de.bonndan.nivio.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class Indexer {

    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    private final LandscapeRepository landscapeRepo;
    private final ServiceRepository serviceRepo;
    private final NotificationService notificationService;

    @Autowired
    public Indexer(LandscapeRepository environmentRepo, ServiceRepository serviceRepo, NotificationService notificationService) {
        this.landscapeRepo = environmentRepo;
        this.serviceRepo = serviceRepo;
        this.notificationService = notificationService;
    }

    public Landscape reIndex(final Environment input) {
        Landscape landscape = landscapeRepo.findDistinctByIdentifier(input.getIdentifier());
        if (landscape == null) {
            logger.info("Creating new landscape " + input.getIdentifier());
            landscape = input.toLandscape();
        } else {
            landscape.setName(input.getName());
            landscape.setContact(input.getContact());
        }

        try {
            diff(input, landscape);
            linkDataflow(input, landscape);
            landscapeRepo.save(landscape);
        } catch (ProcessingException e) {
            final String msg = "Error while reindexing landscape " + input.getIdentifier();
            logger.warn(msg, e);
            notificationService.sendError(e, msg);
        }
        return landscape;
    }

    private void diff(final Environment environment, final Landscape landscape) {

        List<Service> existingServices = serviceRepo.findAllByLandscape(landscape);

        //insert new ones
        List<LandscapeItem> newItems = LandscapeItems.added(environment.getServiceDescriptions(), existingServices);
        List<Service> inLandscape = new ArrayList<>();
        logger.info("Adding " + newItems.size() + " items in env " + landscape.getIdentifier());
        newItems.forEach(
                serviceDescription -> {
                    logger.info("Creating new service " + serviceDescription.getIdentifier() + " in env " + environment.getIdentifier());
                    Service created = ServiceFactory.fromDescription(serviceDescription, landscape);
                    serviceRepo.save(created);

                    landscape.addService(created);
                }
        );

        //update existing
        List<LandscapeItem> kept = LandscapeItems.kept(environment.getServiceDescriptions(), existingServices);
        logger.info("Updating " + kept.size() + " services in landscape " + landscape.getIdentifier());
        kept.forEach(
                service -> {
                    logger.info("Updating service " + service.getIdentifier() + " in landscape " + environment.getIdentifier());
                    ServiceDescription description = getDescription(service, environment);

                    ServiceFactory.assignAll((Service) service, description);
                    inLandscape.add((Service) service);
                }
        );

        landscape.setServices(inLandscape);
        linkAllProviders(inLandscape, environment);
        deleteUnreferenced(environment, inLandscape, existingServices);
    }

    private static ServiceDescription getDescription(LandscapeItem service, final Environment environment) {
        ServiceDescription description = environment.getServiceDescriptions().stream()
                .filter(d -> d.getIdentifier().equals(service.getIdentifier()))
                .findFirst().orElse(null);

        if (description == null)
            throw new ProcessingException(environment, "Could not find service description for service " + service);

        return description;
    }

    private void deleteUnreferenced(final Environment environment, List<Service> kept, List<Service> all) {
        List<LandscapeItem> removed = LandscapeItems.removed(kept, all);
        logger.info("Removing " + removed.size() + " sources in env " + environment.getIdentifier());
        removed.forEach(
                service -> {
                    logger.info("Service " + service.getIdentifier() + " not contained anymore in env " + environment.getIdentifier() + ", deleting it.");
                    serviceRepo.delete((Service) service);
                }
        );
    }

    /**
     * Links all providers to a service
     */
    private void linkAllProviders(List<Service> services, Environment environment) {

        services.forEach(
                service -> {
                    ServiceDescription description = getDescription(service, environment);
                    description.getProvided_by().forEach(providerName -> {
                        Service provider = services.stream()
                                .filter(s -> s.getIdentifier().equals(providerName))
                                .findFirst().orElse(null);

                        if (provider == null) {
                            throw new ProcessingException(environment, "Could not find service " + provider + " in landscape " + environment);
                        }

                        if (!Utils.contains(provider, service.getProvidedBy())) {
                            service.getProvidedBy().add(provider);
                            logger.info("Adding provider " + provider.getIdentifier() + " to " + service.getIdentifier());
                        }
                    });
                }
        );
    }

    private void linkDataflow(final Environment input, final Landscape landscape) {
        input.getServiceDescriptions().forEach(serviceDescription -> {
            Service service = Utils.pick(serviceDescription.getIdentifier(), landscape.getServices());
            serviceDescription.getDataFlow().forEach(description -> {
                Service target = Utils.find(description.getTarget(), landscape.getServices());
                if (target == null) {
                    logger.warn("Dataflow target service " + description.getTarget() + " not found");
                    return;
                }
                Iterator<DataFlowItem> iterator = service.getDataFlow().iterator();
                DataFlow existing = null;
                DataFlow dataFlow = new DataFlow(service, target);
                while (iterator.hasNext()) {
                    existing = (DataFlow) iterator.next();
                    if (existing.equals(dataFlow)) {
                        existing.setDescription(description.getDescription());
                        existing.setFormat(description.getFormat());
                        break;
                    }
                    existing = null;
                }

                if (existing != null) {
                    dataFlow.setDescription(description.getDescription());
                    dataFlow.setFormat(description.getFormat());

                    service.getDataFlow().add(dataFlow);
                }

                logger.info("Creating dataflow between " + service.getIdentifier() + " and " + target.getIdentifier());
            });
        });
    }
}