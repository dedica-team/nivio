package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.kpi.KPIFactory;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.util.SafeAssign;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandscapeFactory {

    public static final List<String> DEFAULT_GROUP_NAMES = List.of(Layer.infrastructure.name(), Layer.domain.name());

    private static final KPIFactory kpiFactory = new KPIFactory();

    private LandscapeFactory() {
    }

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
        DEFAULT_GROUP_NAMES.forEach(s -> groups.put(s, new Group(s, landscapeIdentifier)));
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
                .withGroups(getGroups(identifier));
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

        final boolean isPartial = input.isPartial();

        //overwrite some data which is not handled by resolvers
        if (!isPartial || input.getContact() != null) {
            builder.withContact(input.getContact());
        }
        if (!isPartial || StringUtils.hasLength(input.getName())) {
            builder.withName(input.getName());
        }
        if (!isPartial || input.getDescription() != null) {
            builder.withDescription(input.getDescription());
        }
        if (!isPartial || input.getOwner() != null) {
            builder.withOwner(input.getOwner());
        }

        if (isPartial) {
            builder.withConfig(existing.getConfig().merge(input.getConfig()));
        } else {
            builder.withConfig(input.getConfig());
        }

        if (isPartial) {
            builder.withKpis(kpiFactory.merge(input.getConfig().getKPIs(), existing.getKpis()));
        } else {
            builder.withKpis(kpiFactory.getConfiguredKPIs(input.getConfig().getKPIs()));
        }

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
