package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ServiceDescription;

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
        if (increment.getStatuses() != null)
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

        if (increment.getDataFlow() != null)
            existing.setDataFlow(increment.getDataFlow());
        if (increment.getInterfaces() != null)
            existing.setInterfaces(increment.getInterfaces());
        if (increment.getProvided_by() != null)
            existing.setProvided_by(increment.getProvided_by());
        if (increment.getNetworks() != null)
            existing.setNetworks(increment.getNetworks());
        if (increment.getMachine() != null)
            existing.setMachine(increment.getMachine());

        if (increment.getSoftware() != null)
            existing.setSoftware(increment.getSoftware());
        if (increment.getStatuses() != null)
            existing.setStatuses(increment.getStatuses());
        if (increment.getScale() != null)
            existing.setScale(increment.getScale());
        if (increment.getHost_type() != null)
            existing.setHost_type(increment.getHost_type());
    }

}
