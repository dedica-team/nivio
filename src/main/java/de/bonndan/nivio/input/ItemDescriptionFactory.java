package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.Lifecycle;

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
        if (increment.getShort_name() != null)
            existing.setShort_name(increment.getShort_name());
        if (increment.getIcon() != null)
            existing.setIcon(increment.getIcon());
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
        assignSafe(increment.getHost_type(), existing::setHost_type);

        assignSafe(increment.getCosts(), existing::setCosts);
        assignSafe(increment.getCapability(), existing::setCapability);

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

    /**
     * Writes the values of the second object to the first where first is null.
     *
     * @param item target
     * @param template source
     */
    static void assignTemplateValues(ItemDescription item, ItemDescription template) {

        assignSafeIfAbsent(template.getType(), item.getType(), item::setType);

        assignSafeIfAbsent(template.getLayer(), item.getLayer(), item::setLayer);

        assignSafeIfAbsent(template.getDescription(), item.getDescription(), item::setDescription);

        assignSafeIfAbsent(template.getIcon(), item.getIcon(), item::setIcon);

        assignSafeIfAbsent(template.getNote(), item.getNote(), item::setNote);

        assignSafeIfAbsent(template.getHomepage(), item.getHomepage(), item::setHomepage);

        assignSafeIfAbsent(template.getRepository(), item.getRepository(), item::setRepository);

        assignSafeIfAbsent(template.getContact(), item.getContact(), item::setContact);

        assignSafeIfAbsent(template.getOwner(), item.getOwner(), item::setOwner);

        assignSafeIfAbsent(template.getTeam(), item.getTeam(), item::setTeam);

        assignSafeIfAbsent(template.getGroup(), item.getGroup(), item::setGroup);

        assignSafeIfAbsent(template.getMachine(), item.getMachine(), item::setMachine);

        assignSafeIfAbsent(template.getSoftware(), item.getSoftware(), item::setSoftware);

        assignSafeIfAbsent(template.getVersion(), item.getVersion(), item::setVersion);

        assignSafeIfAbsent(template.getVisibility(), item.getVisibility(), item::setVisibility);

        assignLifecycleIfAbsent(template.getLifecycle(), item.getLifecycle(), item::setLifecycle);

        assignSafeIfAbsent(template.getScale(), item.getScale(), item::setScale);

        assignSafeIfAbsent(template.getHost_type(), item.getHost_type(), item::setHost_type);

        if (template.getTags() != null && item.getTags() == null)
            item.setTags(template.getTags());

        template.getLabels().forEach((s, s2) -> item.getLabels().putIfAbsent(s,s2));


        if (template.getStatuses() != null) {
            template.getStatuses().forEach(statusItem -> {
                if (!item.getStatuses().contains(statusItem))
                    item.getStatuses().add(statusItem);
            });
        }

        if (template.getDataFlow() != null) {
            template.getDataFlow().forEach(dataFlowItem -> {
                if (!item.getDataFlow().contains(dataFlowItem))
                    item.addDataFlow(dataFlowItem);
            });
        }

        if (template.getInterfaces() != null) {
            template.getInterfaces().forEach(interfaceItem -> {
                if (!item.getInterfaces().contains(interfaceItem))
                    item.getInterfaces().add(interfaceItem);
            });
        }

        if (template.getProvided_by() != null) {
            template.getProvided_by().forEach(provider -> {
                if (!item.getProvided_by().contains(provider))
                    item.getProvided_by().add(provider);
            });
        }

        if (template.getNetworks() != null) {
            template.getNetworks().forEach(net -> item.getNetworks().add(net));
        }
    }

    private static void assignLifecycleIfAbsent(Lifecycle s, Lifecycle absent, Consumer<Lifecycle> c) {
        if (s != null && absent == null) c.accept(s);
    }
}
