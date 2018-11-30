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

    /**
     * Assigns all values from the description except data flow and provided_by/provides
     */
    public static void assignAll(Service service, LandscapeItem description) {
        if (description == null) {
            logger.warn("ServiceDescription for service " + service.getIdentifier() + " is null in assignAllValues");
            return;
        }
        service.setName(description.getName());
        service.setLayer(description.getLayer() != null ? description.getLayer() : LandscapeItem.LAYER_APPLICATION);
        service.setType(description.getType() != null ? description.getType() : LandscapeItem.TYPE_SERVICE);

        service.setNote(description.getNote());
        service.setShort_name(description.getShort_name());
        service.setDescription(description.getDescription());
        service.setTags(description.getTags());
        service.setOwner(description.getOwner());

        service.setSoftware(description.getSoftware());
        service.setVersion(description.getVersion());
        service.setInterfaces(description.getInterfaces());

        service.setHomepage(description.getHomepage());
        service.setRepository(description.getRepository());
        service.setContact(description.getContact());
        service.setTeam(description.getTeam());

        service.setVisibility(description.getVisibility());
        service.setGroup(description.getGroup());

        if (service.getStatuses() == null)
            service.setStatuses(description.getStatuses());
        else
            service.getStatuses().putAll(description.getStatuses());

        service.setHost_type(description.getHost_type());
        service.setNetworks(description.getNetworks());
        service.setMachine(description.getMachine());
        service.setScale(description.getScale());
    }
}
