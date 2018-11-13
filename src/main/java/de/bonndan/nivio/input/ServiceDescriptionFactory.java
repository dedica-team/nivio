package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ServiceDescription;

import java.util.Arrays;
import java.util.List;

public interface ServiceDescriptionFactory {

    List<ServiceDescription> fromString(String source);

    static void assignNotNull(ServiceDescription existing, ServiceDescription increment) {
        if (increment.getName() != null)
            existing.setName(increment.getName());
        if (increment.getDescription() != null)
            existing.setDescription(increment.getDescription());
        if (increment.getShort_name() != null)
            existing.setShort_name(increment.getShort_name());
        if (increment.getHomepage() != null)
            existing.setHomepage(increment.getHomepage());
        if (increment.getRepository() != null)
            existing.setRepository(increment.getRepository());
        if (increment.getContact() != null)
            existing.setContact(increment.getContact());
        if (increment.getStatuses() != null) {
            increment.getStatuses().forEach((s, s2) -> existing.getStatuses().put(s, s2));
        }
            existing.setStatuses(increment.getStatuses());
        if (increment.getOwner() != null)
            existing.setOwner(increment.getOwner());
        if (increment.getTeam() != null)
            existing.setTeam(increment.getTeam());
        if (increment.getGroup() != null)
            existing.setGroup(increment.getGroup());
        if (increment.getNote() != null)
            existing.setNote(increment.getNote());
        if (increment.getTags() != null)
            existing.setTags(increment.getTags());

        if (increment.getDataFlow() != null) {
            increment.getDataFlow().forEach(df -> existing.getDataFlow().add(df));
        }

        if (increment.getInterfaces() != null) {
            increment.getInterfaces().forEach(intf -> existing.getInterfaces().add(intf));
        }

        if (increment.getProvided_by() != null) {
            increment.getProvided_by().forEach(s -> existing.getProvided_by().add(s));
        }

        if (increment.getNetworks() != null) {
            increment.getNetworks().forEach(net -> existing.getNetworks().add(net));
        }

        if (increment.getMachine() != null)
            existing.setMachine(increment.getMachine());

        if (increment.getSoftware() != null)
            existing.setSoftware(increment.getSoftware());
        if (increment.getScale() != null)
            existing.setScale(increment.getScale());
        if (increment.getHost_type() != null)
            existing.setHost_type(increment.getHost_type());
    }

}
