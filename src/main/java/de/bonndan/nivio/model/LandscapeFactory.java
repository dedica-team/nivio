package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.kpi.KPIFactory;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LandscapeFactory {

    private static final KPIFactory kpiFactory = new KPIFactory();

    private LandscapeFactory() {}

    /**
     * Creates a new landscape impl.
     *
     * @param input the description
     */
    public static Landscape createFromInput(@NonNull final LandscapeDescription input) {

        var landscape = new Landscape(
                input.getIdentifier(),
                getGroups(input.getIdentifier()),
                input.getName(),
                input.getContact(),
                input.getOwner(),
                input.getDescription(),
                input.getSource(),
                input.getConfig(),
                new ProcessLog(LoggerFactory.getLogger(LandscapeFactory.class), input.getIdentifier()),
                kpiFactory.getConfiguredKPIs(input.getConfig().getKPIs())
        );
        input.getLabels().forEach((s, s2) -> landscape.getLabels().put(s, s2));
        input.getLinks().forEach((s, link) -> landscape.getLinks().put(s, link));

        landscape.getLog().info("Created new landscape from input " + input.getIdentifier());

        return landscape;
    }

    private static Map<String, Group> getGroups(final String landscapeIdentifier) {
        Map<String, Group> groups = new HashMap<>();
        groups.put(Group.COMMON, new Group(Group.COMMON, landscapeIdentifier));
        return groups;
    }

    /**
     * This factory method can be used to create landscapes for testing.
     *
     * @param identifier landscape identifier
     * @return new landscape
     */
    public static LandscapeBuilder createForTesting(@NonNull String identifier, @NonNull String name) {
        return LandscapeBuilder.aLandscape()
                .withIdentifier(identifier)
                .withName(name)
                .withGroups(new HashMap<>(Map.of(Group.COMMON, new Group(Group.COMMON, identifier))));
    }

    /**
     * Returns a copy of the existing landscape, modified with data from input.
     *
     * @param existing the existing landscape, to be replaced
     * @param input    int input data
     * @return a new copy
     */
    public static Landscape recreate(Landscape existing, LandscapeDescription input) {
        LandscapeBuilder builder = LandscapeBuilder.aLandscape()
                .withIdentifier(existing.getIdentifier())
                .withName(existing.getName())
                .withConfig(existing.getConfig())
                .withDescription(existing.getDescription())
                .withContact(existing.getContact())
                .withOwner(existing.getOwner())
                .withSource(existing.getSource())
                .withGroups(existing.getGroups())
                .withItems(existing.getItems().all());

        //overwrite some data which is not handled by resolvers
        builder.withContact(input.getContact());
        if (StringUtils.hasLength(input.getName())) {
            builder.withName(input.getName());
        }
        builder.withConfig(input.getConfig());
        builder.withDescription(input.getDescription());
        builder.withOwner(input.getOwner());
        builder.withKpis(kpiFactory.getConfiguredKPIs(input.getConfig().getKPIs()));

        //merge labels
        Map<String, String> labels = existing.getLabels();
        labels.putAll(input.getLabels());
        builder.withLabels(labels);

        //merge links
        Map<String, Link> links = existing.getLinks();
        links.putAll(input.getLinks());
        builder.withLinks(links);

        return builder.build();
    }
}
