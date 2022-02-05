package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.assessment.kpi.KPIFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.search.NullSearchIndex;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LandscapeFactory {

    private static final KPIFactory kpiFactory = new KPIFactory();

    private LandscapeFactory() {
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
                .withName(name);
    }

    /**
     * Returns a copy of the existing landscape, modified with data from input.
     *
     * @param builder containing the values of the existing landscape  to be replaced
     * @param input   int input data
     * @return a new copy
     */
    public static Landscape recreate(@NonNull final LandscapeBuilder builder,
                                     @NonNull final LandscapeDescription input
    ) {
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
            builder.withConfig(builder.getConfig().merge(input.getConfig()));
        } else {
            builder.withConfig(input.getConfig());
        }

        if (isPartial) {
            Map<String, KPI> map = new HashMap<>(builder.getKpis());
            map.putAll(kpiFactory.getConfiguredKPIs(input.getConfig().getKPIs()));
            builder.withKpis(map);
        } else {
            builder.withKpis(kpiFactory.getConfiguredKPIs(input.getConfig().getKPIs()));
        }

        //merge labels
        Map<String, String> labels = builder.getLabels();
        labels.putAll(input.getLabels());
        builder.withLabels(labels);

        //merge links
        Map<String, Link> links = builder.getLinks();
        links.putAll(input.getLinks());
        builder.withLinks(links);

        return builder.build();
    }

    public static Landscape createIntermediate(String identifier) {
        return new Landscape(identifier, identifier, null, null, null, null, null, null, new HashMap<>(), new Index(new NullSearchIndex()));
    }
}
