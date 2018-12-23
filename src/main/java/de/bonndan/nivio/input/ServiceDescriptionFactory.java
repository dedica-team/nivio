package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ServiceDescription;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;

public interface ServiceDescriptionFactory {

    List<ServiceDescription> fromString(String source);

    static void assignNotNull(ServiceDescription existing, ServiceDescription increment) {
        if (increment.getName() != null)
            existing.setName(increment.getName());
        if (increment.getType() != null)
            existing.setType(increment.getType());
        if (increment.getLayer() != null)
            existing.setLayer(increment.getLayer());
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

        if (increment.getMachine() != null)
            existing.setMachine(increment.getMachine());

        if (increment.getSoftware() != null)
            existing.setSoftware(increment.getSoftware());

        assignSafe(increment.getScale(), existing::setScale);
        assignSafe(increment.getHost_type(), existing::setHost_type);

        /*
         * the rest is merged
         */
        if (increment.getStatuses() != null) {
            increment.getStatuses().forEach(existing::setStatus);
        }

        assignSafe(increment.getDataFlow(), (df) -> df.forEach(existing::addDataFlow));

        assignSafe(increment.getInterfaces(), (set) -> set.forEach(intf -> existing.getInterfaces().add(intf)));

        assignSafe(increment.getProvided_by(), (providers) -> providers.forEach(s -> existing.getProvided_by().add(s)));

        assignSafe(increment.getNetworks(), (nets) -> nets.forEach(net -> existing.getNetworks().add(net)));

    }
}
