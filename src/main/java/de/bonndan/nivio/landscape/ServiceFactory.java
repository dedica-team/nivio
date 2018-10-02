package de.bonndan.nivio.landscape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);

    public static Service fromDescription(LandscapeItem item, Landscape landscape) {
        if (item == null) {
            throw new RuntimeException("landscape item is null");
        }

        Service service = new Service();
        service.setLandscape(landscape);
        service.setIdentifier(item.getIdentifier());
        assignAll(service, item);
        return service;
    }

    public static void assignAll(Service service, LandscapeItem serviceDescription) {
        if (serviceDescription == null) {
            logger.warn("ServiceDescription for service " + service.getIdentifier() + " is null in assignAllValues");
            return;
        }
        service.setName(serviceDescription.getName());
        service.setType(serviceDescription.getType());

        service.setNote(serviceDescription.getNote());
        service.setShort_name(serviceDescription.getShort_name());
        service.setDescription(serviceDescription.getDescription());
        service.setTags(serviceDescription.getTags());
        service.setOwner(serviceDescription.getOwner());

        service.setSoftware(serviceDescription.getSoftware());
        service.setVersion(serviceDescription.getVersion());
        service.setDataFlow(toDataFlow(serviceDescription.getDataFlow(), service.getLandscape()));
        service.setInterfaces(serviceDescription.getInterfaces());

        service.setHomepage(serviceDescription.getHomepage());
        service.setRepository(serviceDescription.getRepository());
        service.setContact(serviceDescription.getContact());
        service.setTeam(serviceDescription.getTeam());

        service.setVisibility(serviceDescription.getVisibility());
        service.setGroup(serviceDescription.getGroup());

        service.setHost_type(serviceDescription.getHost_type());
        service.setNetworks(serviceDescription.getNetworks());
        service.setMachine(serviceDescription.getMachine());
        service.setScale(serviceDescription.getScale());
    }

    private static Set<DataFlowItem> toDataFlow(Set<DataFlowItem> dataFlow, Landscape landscape) {
        DataFlowFactory dataFlowFactory = new DataFlowFactory();
        HashSet<DataFlowItem> objects = new HashSet<>();
        dataFlow.forEach(
                dataFlowItem -> objects.add(dataFlowFactory.createFrom(dataFlowItem, landscape.getServices()))
        );
        return objects;
    }

}
