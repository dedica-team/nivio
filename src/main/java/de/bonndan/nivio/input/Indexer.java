
package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.*;
import de.bonndan.nivio.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class Indexer {

    private static final Logger _logger = LoggerFactory.getLogger(Indexer.class);

    private final LandscapeRepository landscapeRepo;
    private final ServiceRepository serviceRepo;
    private final NotificationService notificationService;

    @Autowired
    public Indexer(LandscapeRepository environmentRepo,
                   ServiceRepository serviceRepo,
                   NotificationService notificationService
    ) {
        this.landscapeRepo = environmentRepo;
        this.serviceRepo = serviceRepo;
        this.notificationService = notificationService;
    }

    public ProcessLog reIndex(final Environment input) {

        ProcessLog logger = new ProcessLog(_logger);

        Landscape landscape = landscapeRepo.findDistinctByIdentifier(input.getIdentifier());
        if (landscape == null) {
            logger.info("Creating new landscape " + input.getIdentifier());
            landscape = input.toLandscape();
            landscapeRepo.save(landscape);
        } else {
            landscape.setName(input.getName());
            landscape.setContact(input.getContact());
        }
        logger.setLandscape(landscape);

        try {
            diff(input, landscape, logger);
            linkDataflow(input, landscape, logger);
            landscapeRepo.save(landscape);
        } catch (ProcessingException e) {
            final String msg = "Error while reindexing landscape " + input.getIdentifier();
            logger.warn(msg, e);
            notificationService.sendError(e, msg);
        }

        logger.info("Reindexed landscape " + input.getIdentifier());
        return logger;
    }

    private void diff(final Environment environment, final Landscape landscape, ProcessLog logger) {

        List<Service> existingServices = serviceRepo.findAllByLandscape(landscape);

        //insert new ones
        List<ServiceItem> newItems = ServiceItems.added(environment.getServiceDescriptions(), existingServices);
        List<Service> inLandscape = new ArrayList<>();
        logger.info("Adding " + newItems.size() + " items in env " + landscape.getIdentifier());
        newItems.forEach(
                serviceDescription -> {
                    logger.info("Creating new service " + serviceDescription.getIdentifier() + " in env " + environment.getIdentifier());
                    Service created = ServiceFactory.fromDescription(serviceDescription, landscape);
                    serviceRepo.save(created);

                    landscape.addService(created);
                    inLandscape.add(created);
                }
        );

        //update existing
        List<ServiceItem> kept = new ArrayList<>();
        if (environment.isIncrement()) {
            kept.addAll(existingServices); //we want to keep all, increment does not contain all services
        } else {
            kept = ServiceItems.kept(environment.getServiceDescriptions(), existingServices);
        }
        logger.info("Updating " + kept.size() + " services in landscape " + landscape.getIdentifier());
        kept.forEach(
                service -> {

                    ServiceDescription description = (ServiceDescription) ServiceItems.find(service.getFullyQualifiedIdentifier(), environment.getServiceDescriptions());
                    if (description == null) {
                        if (environment.isIncrement()) {
                            inLandscape.add((Service) service);
                            return;
                        } else {
                            throw new ProcessingException(environment, "Service not found " + service.getIdentifier());
                        }
                    }

                    logger.info("Updating service " + service.getIdentifier() + " in landscape " + environment.getIdentifier());

                    ServiceFactory.assignAll((Service) service, description);
                    inLandscape.add((Service) service);
                }
        );

        landscape.setServices(inLandscape);
        linkAllProviders(inLandscape, environment, logger);
        deleteUnreferenced(environment, inLandscape, existingServices, logger);
    }

    private void deleteUnreferenced(final Environment environment, List<Service> kept, List<Service> all, ProcessLog logger) {
        if (environment.isIncrement()) {
            logger.info("Incremental change, will not remove any unreferenced services.");
            return;
        }

        List<ServiceItem> removed = ServiceItems.removed(kept, all);
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
    private void linkAllProviders(List<Service> services, Environment environment, ProcessLog logger) {

        services.forEach(
                service -> {
                    ServiceDescription description = (ServiceDescription) ServiceItems.find(service.getFullyQualifiedIdentifier(), environment.getServiceDescriptions());
                    if (description == null) {
                        if (environment.isIncrement())
                            return;
                        else
                            throw new ProcessingException(environment, "Service not found " + service.getIdentifier());
                    }
                    description.getProvided_by().forEach(providerName -> {
                        var fqi = FullyQualifiedIdentifier.from(providerName);
                        Service provider = (Service) ServiceItems.find(fqi, services);
                        if (provider == null) {
                            throw new ProcessingException(environment, "Could not find service " + fqi + " in landscape " + environment);
                        }

                        if (!ServiceItems.contains(provider, service.getProvidedBy())) {
                            service.getProvidedBy().add(provider);
                            provider.getProvides().add(service);
                            logger.info("Adding provider " + provider + " to serivce " + service);
                        }
                    });
                }
        );
    }

    private void linkDataflow(final Environment input, final Landscape landscape, ProcessLog logger) {
        input.getServiceDescriptions().forEach(serviceDescription -> {
            Service origin = (Service) ServiceItems.pick(serviceDescription, landscape.getServices());

            serviceDescription.getDataFlow().forEach(description -> {

                var fqi = FullyQualifiedIdentifier.from(description.getTarget());
                Service target = (Service) ServiceItems.find(fqi, landscape.getServices());
                if (target == null) {
                    logger.warn("Dataflow target service " + description.getTarget() + " not found");
                    return;
                }
                Iterator<DataFlowItem> iterator = origin.getDataFlow().iterator();
                DataFlow existing = null;
                DataFlow dataFlow = new DataFlow(origin, target);
                while (iterator.hasNext()) {
                    existing = (DataFlow) iterator.next();
                    if (existing.equals(dataFlow)) {
                        logger.info("Updating dataflow " + existing);
                        existing.setDescription(description.getDescription());
                        existing.setFormat(description.getFormat());
                        break;
                    }
                    existing = null;
                }

                if (existing == null) {
                    dataFlow.setDescription(description.getDescription());
                    dataFlow.setFormat(description.getFormat());

                    origin.getDataFlow().add(dataFlow);
                    logger.info("Adding dataflow " + existing);
                }

                logger.info("Creating dataflow between " + origin.getIdentifier() + " and " + target.getIdentifier());
            });
        });
    }
}