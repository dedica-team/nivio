package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.*;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;

import static de.bonndan.nivio.util.SafeAssign.assignSafeIfAbsent;

/**
 * Responsible to apply template to landscape items.
 * <p>
 * Resolves the items the templates are assigned to as well as dynamic endpoints of relation in templates.
 */
public class TemplateResolver {

    /**
     * Applies the template values and relations to all items the template is assigned to.
     *
     * @param landscape           the landscape containing all(!) items. Querying happens on these items.
     * @param templatesAndTargets The assignment can be a list of static item identifiers or dynamic queries to match
     *                            certain items.
     */
    public void processTemplates(LandscapeDescription landscape, Map<ItemDescription, List<String>> templatesAndTargets) {
        templatesAndTargets.forEach((landscapeItem, identifiers) -> {
            applyTemplateValues(landscapeItem, identifiers, landscape);
        });

        templatesAndTargets.forEach((landscapeItem, identifiers) -> {
            resolveTemplateRelations(landscapeItem, identifiers, landscape);
        });
    }

    /**
     * Applies the template values to all items the template is assigned to.
     */
    private void applyTemplateValues(
            ItemDescription template,
            List<String> templateTargets,
            LandscapeDescription landscape
    ) {
        templateTargets.forEach(term ->
                ServiceItems.query(term, landscape.getItemDescriptions())
                        .forEach(item -> assignTemplateValues((ItemDescription) item, template))
        );
    }


    /**
     * Writes the values of the template (second object) to the first where first is null.
     *
     * @param item     target
     * @param template source
     */
    static void assignTemplateValues(ItemDescription item, ItemDescription template) {

        assignSafeIfAbsent(template.getType(), item.getType(), item::setType);

        assignSafeIfAbsent(template.getLayer(), item.getLayer(), item::setLayer);

        assignSafeIfAbsent(template.getDescription(), item.getDescription(), item::setDescription);

        assignSafeIfAbsent(template.getIcon(), item.getIcon(), item::setIcon);

        assignSafeIfAbsent(template.getNote(), item.getNote(), item::setNote);

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

        assignSafeIfAbsent(template.getHostType(), item.getHostType(), item::setHostType);

        if (template.getTags() != null && item.getTags() == null)
            item.setTags(template.getTags());

        template.getLabels().forEach((s, s2) -> item.getLabels().putIfAbsent(s, s2));


        if (template.getStatuses() != null) {
            template.getStatuses().forEach(statusItem -> {
                if (!item.getStatuses().contains(statusItem))
                    item.getStatuses().add(statusItem);
            });
        }

        if (template.getInterfaces() != null) {
            template.getInterfaces().forEach(interfaceItem -> {
                if (!item.getInterfaces().contains(interfaceItem))
                    item.getInterfaces().add(interfaceItem);
            });
        }

        if (template.getNetworks() != null) {
            template.getNetworks().forEach(net -> item.getNetworks().add(net));
        }
    }


    private static void assignLifecycleIfAbsent(Lifecycle s, Lifecycle absent, Consumer<Lifecycle> c) {
        if (s != null && absent == null) c.accept(s);
    }


    /**
     * Finds relation named in queries.
     */
    private void resolveTemplateRelations(
            ItemDescription template,
            List<String> templateTargets,
            LandscapeDescription landscape
    ) {
        templateTargets.forEach(term ->
                ServiceItems.query(term, landscape.getItemDescriptions())
                        .forEach(item -> resolveTemplateRelations((ItemDescription) item, template, landscape.getItemDescriptions()))
        );
    }

    private static void resolveTemplateRelations(
            ItemDescription description,
            ItemDescription template,
            List<ItemDescription> allItems
    ) {

        //providers
        template.getProvidedBy().forEach(term -> {
            ServiceItems.query(term, allItems).stream().findFirst().ifPresent(o -> {
                RelationDescription rel = RelationBuilder.createProviderDescription((ItemDescription) o, description.getIdentifier());
                description.addRelation(rel);
            });
        });

        //other relations
        template.getRelations().forEach(rel -> {

            List<ItemDescription> sources = new ArrayList<>();
            if (StringUtils.isEmpty(rel.getSource())) {
                sources.add(description);
            } else {
                ServiceItems.query(rel.getSource(), allItems).forEach(o -> sources.add((ItemDescription) o));
            }

            sources.forEach(source -> {
                RelationDescription copy = RelationBuilder.copy(rel);
                copy.setSource(source.getFullyQualifiedIdentifier().toString());
                resolveTarget(copy, allItems);
                source.addRelation(copy);
            });
        });

    }

    private static void resolveTarget(RelationDescription rel, List<ItemDescription> allItems) {
        ServiceItems.query(rel.getTarget(), allItems).stream().findFirst()
                .ifPresent(item -> rel.setTarget(item.getFullyQualifiedIdentifier().toString()));
    }
}
