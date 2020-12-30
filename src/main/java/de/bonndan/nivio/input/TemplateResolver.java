package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible to apply templates to landscape items.
 * <p>
 * Resolves the items the templates are assigned to as well as dynamic endpoints of relation in templates.
 */
public class TemplateResolver extends Resolver {

    protected TemplateResolver(ProcessLog processLog) {
        super(processLog);
    }

    /**
     * Applies the template values and relations to all items the template is assigned to.
     *
     * @param landscape the landscape containing all(!) items. Querying happens on these items.
     */
    @Override
    public void resolve(LandscapeDescription landscape) {

        Map<ItemDescription, List<String>> templatesAndTargets = new HashMap<>();
        landscape.getSourceReferences().forEach(ref -> {
            ref.getAssignTemplates().forEach((key, identifiers) -> {
                ItemDescription template = landscape.getTemplates().get(key);
                template.setName(null);
                templatesAndTargets.put(template, identifiers);
            });
        });

        templatesAndTargets.forEach((template, identifiers) -> applyTemplateValues(template, identifiers, landscape));
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
                        .forEach(item -> ItemDescriptionValues.assignSafeNotNull(item, template))
        );
    }
}
