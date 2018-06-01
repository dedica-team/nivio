package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.ServiceDescriptionFactory;
import de.bonndan.nivio.landscape.LandscapeItem;
import de.bonndan.nivio.landscape.Service;
import de.bonndan.nivio.landscape.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.List;

@Component
public class Indexer implements ApplicationListener<FSChangeEvent> {

    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    private final EnvironmentRepo environmentRepo;
    private final ServiceRepository serviceRepo;

    @Autowired
    public Indexer(EnvironmentRepo environmentRepo, ServiceRepository serviceRepo) {
        this.environmentRepo = environmentRepo;
        this.serviceRepo = serviceRepo;
    }

    @Override
    public void onApplicationEvent(FSChangeEvent fsChangeEvent) {
        String s = (fsChangeEvent.getEvent().context()).toString();
        try {
            Environment environment = EnvironmentFactory.fromYaml(
                    new File(DirectoryWatcher.NIVIO_ENV_DIRECTORY + "/" + s)
            );

            for (Source source : environment.getSources()) {
                ServiceDescription serviceDescription = ServiceDescriptionFactory.fromYaml(new File(source.getUrl()));
                environment.addService(serviceDescription);
            }

            reindex(environment);

        } catch (ReadingException e) {
            logger.error("Failed to read " + DirectoryWatcher.NIVIO_ENV_DIRECTORY + "/" + s, e);
        }
    }

    public void reindex(Environment environment) {
        Environment existing = environmentRepo.findDistinctByIdentifier(environment.getIdentifier());
        if (existing == null) {
            existing = environment;
        } else {
            existing.setName(environment.getName());
            existing.setSources(environment.getSources());
        }
        environmentRepo.save(existing);

        //delete services which are not listed anymore
        List<de.bonndan.nivio.landscape.Service> existingServices = serviceRepo.findAllByEnvironment(environment.getIdentifier());
        LandscapeItems.removed(environment.getServiceDescriptions(), existingServices).forEach(
                service -> {
                    logger.info("Service " + service.getIdentifier() + " not contained anymore in env " + environment.getIdentifier() + ", deleting it.");
                    serviceRepo.delete((Service) service);
                }
        );

        //insert new ones
        LandscapeItems.added(environment.getServiceDescriptions(), existingServices).forEach(
                serviceDescription -> {
                    logger.info("Creating new service " + serviceDescription.getIdentifier() + " in env " + environment.getIdentifier());
                    Service created = new Service();
                    created.setIdentifier(serviceDescription.getIdentifier());
                    assignAllValues(created, (ServiceDescription) serviceDescription);
                    createInfrastructure((ServiceDescription) serviceDescription, existingServices);
                    assignDataflows(created);
                    serviceRepo.save(created);
                }
        );

        //update existing
        LandscapeItems.kept(environment.getServiceDescriptions(), existingServices).forEach(
                service -> {
                    ServiceDescription description = environment.getServiceDescriptions().stream()
                            .filter(serviceDescription -> serviceDescription.getIdentifier().equals(service.getIdentifier()))
                            .findFirst().orElse(null);

                    if (description == null)
                        throw new RuntimeException("Could not find service description for service " + service.getIdentifier());

                    assignAllValues((Service) service, description);
                    createInfrastructure(description, existingServices);
                    linkInfrastructure(description);
                    cleanupInfrastructure(environment);
                    serviceRepo.save((Service) service);
                }
        );
    }

    private void cleanupInfrastructure(Environment environment) {
        List<Service> allByEnvironmentAndType = serviceRepo.findAllByEnvironmentAndType(environment.getIdentifier(), LandscapeItem.INFRASTRUCTURE);
        allByEnvironmentAndType.forEach(service -> {
            if (!service.providesAny()) {
                logger.info("Deleting infrastructure item " + service.getIdentifier() + ", no relations to applications");
                serviceRepo.delete(service);
            }
        });
    }

    private void linkInfrastructure(ServiceDescription desc) {
        List<de.bonndan.nivio.landscape.Service> existingServices = serviceRepo.findAllByEnvironment(desc.getEnvironment());
        Service service = existingServices.stream()
                .filter(s -> s.getIdentifier().equals(desc.getIdentifier()))
                .findFirst().orElse(null);
        if (service == null) {
            throw new RuntimeException("Could not find infrastructure " + desc.getIdentifier() + " in env " + desc.getEnvironment());
        }

        //reset all
        service.setProvidedBy(new HashSet<>());

        desc.getInfrastructure().forEach(infraDesc -> {
            existingServices.stream()
                    .filter(s -> s.getIdentifier().equals(infraDesc.getIdentifier()))
                    .findFirst()
                    .ifPresent(service1 -> service.getProvidedBy().add(service));
        });

    }

    private void createInfrastructure(ServiceDescription serviceDescription, List<Service> existingServices) {

        //not deleting infrastructure, will keep it until no more relations exist

        //insert new ones
        LandscapeItems.added(serviceDescription.getInfrastructure(), existingServices).forEach(
                infraItem -> {
                    logger.info("Creating new infrastructure item " + infraItem.getIdentifier() + " in env " + ((ServiceDescription)infraItem).getEnvironment());
                    Service created = new Service();
                    created.setIdentifier(infraItem.getIdentifier());
                    assignAllValues(created, (ServiceDescription) infraItem);
                    created.setType(LandscapeItem.INFRASTRUCTURE);
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

    public void assignDataflows(Service service) {

    }
}