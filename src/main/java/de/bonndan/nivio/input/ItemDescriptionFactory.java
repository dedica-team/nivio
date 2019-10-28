package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.Lifecycle;
import de.bonndan.nivio.model.ServiceItems;

import java.util.List;
import java.util.function.*;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;
import static de.bonndan.nivio.util.SafeAssign.assignSafeIfAbsent;

public interface ItemDescriptionFactory {

    List<ItemDescription> getDescriptions(SourceReference reference);

    static void assignNotNull(ItemDescription existing, ItemDescription increment) {
        
        if (increment.getName() != null)
            existing.setName(increment.getName());
        if (increment.getType() != null)
            existing.setType(increment.getType());
        if (increment.getLayer() != null)
            existing.setLayer(increment.getLayer());
        if (increment.getDescription() != null)
            existing.setDescription(increment.getDescription());
        if (increment.getShortName() != null)
            existing.setShortName(increment.getShortName());
        if (increment.getIcon() != null)
            existing.setIcon(increment.getIcon());
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

        if (increment.getLabels() != null)
            increment.getLabels().forEach((s, s2) -> existing.getLabels().putIfAbsent(s, s2));

        if (increment.getSoftware() != null)
            existing.setSoftware(increment.getSoftware());
        if (increment.getVersion() != null)
            existing.setVersion(increment.getVersion());
        if (increment.getVisibility() != null)
            existing.setVisibility(increment.getVisibility());
        if (increment.getLifecycle() != null)
            existing.setLifecycle(increment.getLifecycle());

        assignSafe(increment.getScale(), existing::setScale);
        assignSafe(increment.getHostType(), existing::setHostType);

        assignSafe(increment.getCosts(), existing::setCosts);
        assignSafe(increment.getCapability(), existing::setCapability);

        /*
         * the rest is merged
         */
        if (increment.getStatuses() != null) {
            increment.getStatuses().forEach(existing::setStatus);
        }

        existing.getLinks().putAll(increment.getLinks());

        assignSafe(increment.getRelations(), (rel) -> rel.forEach(existing::addRelation));

        assignSafe(increment.getInterfaces(), (set) -> set.forEach(intf -> existing.getInterfaces().add(intf)));

        assignSafe(increment.getNetworks(), (nets) -> nets.forEach(net -> existing.getNetworks().add(net)));

    }

}
