package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class Indexer {

    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    private final LandscapeRepository landscapeRepo;
    private final ServiceRepository serviceRepo;

    @Autowired
    public Indexer(LandscapeRepository environmentRepo, ServiceRepository serviceRepo) {
        this.landscapeRepo = environmentRepo;
        this.serviceRepo = serviceRepo;
    }

    public Landscape reIndex(final Environment input) {
        Landscape landscape = landscapeRepo.findDistinctByIdentifier(input.getIdentifier());
        if (landscape == null) {
            logger.info("Creating new landscape " + input.getIdentifier());
            landscape = input.toLandscape();
        } else {
            landscape.setName(input.getName());
        }
        //landscapeRepo.save(landscape);
        diff(input, landscape);
        link(input, landscape);
        landscapeRepo.save(landscape);
        return landscape;
    }

    private void diff(final Environment environment, final Landscape landscape) {

        //delete services which are not listed anymore
        List<Service> existingServices = serviceRepo.findAllByLandscapeAndType(landscape, LandscapeItem.APPLICATION);
        List<LandscapeItem> removed = LandscapeItems.removed(environment.getServiceDescriptions(), existingServices);
        logger.info("Removing " + removed.size() + " sources in env " + landscape.getIdentifier());
        removed.forEach(
                service -> {
                    logger.info("Service " + service.getIdentifier() + " not contained anymore in env " + environment.getIdentifier() + ", deleting it.");
                    serviceRepo.delete((Service) service);
                }
        );

        //insert new ones
        List<LandscapeItem> added = LandscapeItems.added(environment.getServiceDescriptions(), existingServices);
        logger.info("Adding " + added.size() + " sources in env " + landscape.getIdentifier());
        added.forEach(
                serviceDescription -> {
                    logger.info("Creating new service " + serviceDescription.getIdentifier() + " in env " + environment.getIdentifier());
                    Service created = new Service();
                    created.setLandscape(landscape);
                    created.setType(LandscapeItem.APPLICATION);
                    created.setIdentifier(serviceDescription.getIdentifier());
                    assignAllValues(created, (ServiceDescription) serviceDescription);
                    serviceRepo.save(created);

                    createInfrastructure((ServiceDescription) serviceDescription, landscape);
                    linkInfrastructure((ServiceDescription) serviceDescription, landscape);

                    landscape.addService(created);
                }
        );

        //update existing
        List<LandscapeItem> kept = LandscapeItems.kept(environment.getServiceDescriptions(), existingServices);
        logger.info("Updating " + kept.size() + " services in landscape " + landscape.getIdentifier());
        kept.forEach(
                service -> {
                    logger.info("Updating service " + service.getIdentifier() + " in landscape " + environment.getIdentifier());
                    ServiceDescription description = environment.getServiceDescriptions().stream()
                            .filter(serviceDescription -> serviceDescription.getIdentifier().equals(service.getIdentifier()))
                            .findFirst().orElse(null);

                    if (description == null)
                        throw new RuntimeException("Could not find service description for service " + service.getIdentifier());

                    assignAllValues((Service) service, description);
                    createInfrastructure(description, landscape);
                    linkInfrastructure(description, landscape);

                    //landscape.addService((Service)service);
                }
        );
        cleanupInfrastructure(landscape);
    }

    private void link(final Environment input, final Landscape landscape) {
        input.getServiceDescriptions().forEach(serviceDescription -> {
            Service service = landscape.getService(serviceDescription.getIdentifier());
            serviceDescription.getDataFlow().forEach(description -> {
                Service target = landscape.getService(description.getTarget());
                if (target == null) {
                    logger.warn("Dataflow target service " + description.getTarget() + " not found");
                    return;
                }
                Iterator<DataFlow> iterator = service.getDataFlow().iterator();
                DataFlow existing = null;
                DataFlow dataFlow = new DataFlow(service, target);
                while (iterator.hasNext()) {
                    existing = iterator.next();
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

    private void cleanupInfrastructure(Landscape landscape) {
        List<Service> allByEnvironmentAndType = serviceRepo.findAllByLandscapeAndType(landscape, LandscapeItem.INFRASTRUCTURE);
        allByEnvironmentAndType.forEach(service -> {
            if (!service.providesAny()) {
                logger.info("Deleting infrastructure item " + service.getIdentifier() + ", no relations to applications");
                serviceRepo.delete(service);
            }
        });
    }

    private void linkInfrastructure(ServiceDescription desc, Landscape landscape) {
        List<Service> existingServices = serviceRepo.findAllByLandscape(landscape);
        Service service = existingServices.stream()
                .filter(s -> s.getIdentifier().equals(desc.getIdentifier()))
                .findFirst().orElse(null);
        if (service == null) {
            throw new RuntimeException("Could not find service " + desc.getIdentifier() + " in landscape " + desc.getEnvironment());
        }

        desc.getInfrastructure().forEach(infraDesc -> {
            existingServices.stream()
                    .filter(s -> s.getIdentifier().equals(infraDesc.getIdentifier()))
                    .findFirst()
                    .ifPresent(
                            service1 -> {
                                if (!service.getProvidedBy().contains(service1)) {
                                    service.getProvidedBy().add(service1);
                                    logger.info("Adding provider " + service1.getIdentifier() + " to " + service.getIdentifier());
                                }
                            }
                    );
        });
    }

    private void createInfrastructure(ServiceDescription serviceDescription, Landscape landscape) {

        List<Service> existingServices = serviceRepo.findAllByLandscapeAndType(landscape, LandscapeItem.INFRASTRUCTURE);
        //not deleting infrastructure, will keep it until no more relations exist

        //insert new ones
        LandscapeItems.added(serviceDescription.getInfrastructure(), existingServices).forEach(
                infraItem -> {
                    logger.info("Creating new infrastructure item " + infraItem.getIdentifier() + " in env " + ((ServiceDescription) infraItem).getEnvironment());
                    Service created = new Service();
                    created.setIdentifier(infraItem.getIdentifier());
                    logger.info("Creating infrastructure item " + created.getIdentifier());
                    assignAllValues(created, (ServiceDescription) infraItem);
                    created.setType(LandscapeItem.INFRASTRUCTURE);
                    created.setLandscape(landscape);
                }
        );

        //update existing
        LandscapeItems.kept(serviceDescription.getInfrastructure(), existingServices).forEach(
                service -> {
                    ServiceDescription description = serviceDescription.getInfrastructure().stream()
                            .filter(s -> s.getIdentifier().equals(service.getIdentifier()))
                            .findFirst().orElse(null);
                    logger.info("Updating infrastructure item " + service.getIdentifier());
                    assignAllValues((Service) service, description);
                }
        );
    }

    public void assignAllValues(Service service, ServiceDescription serviceDescription) {

        if (serviceDescription == null) {
            logger.warn("ServiceDescription for service " + service.getIdentifier() + " is null in assignAllValues");
            return;
        }
        service.setName(serviceDescription.getName());
        service.setNote(serviceDescription.getNote());
        service.setShort_name(serviceDescription.getName());
        service.setDescription(serviceDescription.getDescription());
        service.setTags(serviceDescription.getTags());

        service.setSoftware(serviceDescription.getSoftware());
        service.setVersion(serviceDescription.getVersion());

        service.setHomepage(serviceDescription.getHomepage());
        service.setRepository(serviceDescription.getRepository());
        service.setContact(serviceDescription.getContact());
        service.setTeam(serviceDescription.getTeam());

        service.setVisibility(serviceDescription.getVisibility());
        service.setGroup(serviceDescription.getGroup());

        service.setHost_type(serviceDescription.getHost_type());
        service.setNetwork_zone(serviceDescription.getNetwork_zone());
        service.setMachine(serviceDescription.getMachine());
        service.setScale(serviceDescription.getScale());
    }
}