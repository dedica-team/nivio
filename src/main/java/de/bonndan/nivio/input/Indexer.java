package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        landscapeRepo.save(landscape);
        diff(input, landscape);
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

                    createInfrastructure((ServiceDescription) serviceDescription, existingServices, landscape);
                    linkInfrastructure((ServiceDescription) serviceDescription, landscape);
                    assignDataflows(created, (ServiceDescription) serviceDescription);

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
                    createInfrastructure(description, existingServices, landscape);
                    linkInfrastructure(description, landscape);

                    serviceRepo.save((Service) service);
                    landscape.addService((Service)service);
                }
        );
        cleanupInfrastructure(landscape);
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
                                service.getProvidedBy().add(service1);
                                service1.getProvides().add(service);
                                logger.info("Adding provider " + service1.getIdentifier() + " to " + service.getIdentifier());

                            }
                    );
        });
        serviceRepo.save(service);
    }

    private void createInfrastructure(ServiceDescription serviceDescription, List<Service> existingServices, Landscape landscape) {

        //not deleting infrastructure, will keep it until no more relations exist

        //insert new ones
        LandscapeItems.added(serviceDescription.getInfrastructure(), existingServices).forEach(
                infraItem -> {
                    logger.info("Creating new infrastructure item " + infraItem.getIdentifier() + " in env " + ((ServiceDescription) infraItem).getEnvironment());
                    Service created = new Service();
                    created.setIdentifier(infraItem.getIdentifier());
                    assignAllValues(created, (ServiceDescription) infraItem);
                    created.setType(LandscapeItem.INFRASTRUCTURE);
                    created.setLandscape(landscape);
                    serviceRepo.save(created);
                }
        );

        //update existing
        LandscapeItems.kept(serviceDescription.getInfrastructure(), existingServices).forEach(
                service -> {
                    ServiceDescription description = serviceDescription.getInfrastructure().stream()
                            .filter(s -> s.getIdentifier().equals(service.getIdentifier()))
                            .findFirst().orElse(null);
                    assignAllValues((Service) service, description);
                    serviceRepo.save((Service) service);
                }
        );
    }

    public void assignAllValues(Service service, ServiceDescription serviceDescription) {

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
        service.setBounded_context(serviceDescription.getBounded_context());

        service.setHost_type(serviceDescription.getHost_type());
        service.setNetwork_zone(serviceDescription.getNetwork_zone());
        service.setMachine(serviceDescription.getMachine());
        service.setScale(serviceDescription.getScale());
    }

    public void assignDataflows(Service service, ServiceDescription serviceDescription) {
        serviceDescription.getDataFlow().forEach(description -> {
            Service target = service.getLandscape().getService(description.getTarget());
            if (target == null) {
                logger.warn("Dataflow target service " + description.getTarget() + " not found");
                return;
            }
            DataFlow dataFlow = new DataFlow(service, target);
            dataFlow.setDescription(description.getDescription());
            dataFlow.setFormat(description.getFormat());
            service.getDataFlow().add(dataFlow);
        });
    }
}