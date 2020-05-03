package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;

import static de.bonndan.nivio.util.SafeAssign.assignSafeIfAbsent;

/**
 * Responsible to apply templates to landscape items.
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
        templatesAndTargets.forEach((landscapeItem, identifiers) -> applyTemplateValues(landscapeItem, identifiers, landscape));
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
                landscape.getItemDescriptions().query(term)
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
        assignSafeIfAbsent(template.getDescription(), item.getDescription(), item::setDescription);
        assignSafeIfAbsent(template.getContact(), item.getContact(), item::setContact);
        assignSafeIfAbsent(template.getOwner(), item.getOwner(), item::setOwner);
        assignSafeIfAbsent(template.getGroup(), item.getGroup(), item::setGroup);
        assignLifecycleIfAbsent(template.getLifecycle(), item.getLifecycle(), item::setLifecycle);

        if (template.getProvidedBy() != null) {
            template.getProvidedBy().stream()
                    .filter(s -> !StringUtils.isEmpty(s) && !item.getProvidedBy().contains(s))
                    .forEach(s -> item.getProvidedBy().add(s));
        }

        template.getRelations().forEach(item::addRelation);

        Labeled.merge(template, item);

        if (template.getInterfaces() != null) {
            template.getInterfaces().forEach(interfaceItem -> {
                if (!item.getInterfaces().contains(interfaceItem))
                    item.getInterfaces().add(interfaceItem);
            });
        }
    }


    private static void assignLifecycleIfAbsent(Lifecycle s, Lifecycle absent, Consumer<Lifecycle> c) {
        if (s != null && absent == null) c.accept(s);
    }

}
