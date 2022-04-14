package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.Source;
import de.bonndan.nivio.search.LuceneSearchIndex;
import org.springframework.lang.NonNull;

import java.util.*;

public final class LandscapeBuilder extends GraphNodeBuilder<LandscapeBuilder, Landscape, Landscape> {

    private Map<String, KPI> kpis = new HashMap<>();
    private Source source;
    private LandscapeConfig config;
    private Index<GraphComponent> index;

    private LandscapeBuilder() {
    }

    @Override
    public LandscapeBuilder getThis() {
        return this;
    }

    public static LandscapeBuilder aLandscape() {
        return new LandscapeBuilder();
    }

    public LandscapeBuilder withKpis(Map<String, KPI> kpis) {
        this.kpis = kpis;
        return this;
    }

    @Override
    public LandscapeBuilder withComponentDescription(ComponentDescription description) {
        if (description instanceof LandscapeDescription) {
            withSource(((LandscapeDescription) description).getSource());
        }
        return super.withComponentDescription(description);
    }

    public LandscapeBuilder withSource(Source source) {
        this.source = source;
        return this;
    }

    public LandscapeBuilder withConfig(LandscapeConfig config) {
        this.config = config;
        return this;
    }

    public LandscapeBuilder withIndex(Index<GraphComponent> index) {
        this.index = index;
        return this;
    }

    public Landscape build() {
        Objects.requireNonNull(identifier, "The landscape identifier cannot be null");

        Landscape landscape = new Landscape(identifier,
                name,
                contact,
                owner,
                description,
                type,
                source,
                config,
                kpis,
                index == null ? new Index<>(LuceneSearchIndex.createFor(identifier)) : index
        );
        landscape.setLinks(links);

        labels.forEach((s, s2) -> landscape.getLabels().put(s, s2));
        return landscape;
    }

    LandscapeConfig getConfig() {
        return config;
    }

    @NonNull
    Map<String, KPI> getKpis() {
        if (kpis == null) {
            return new HashMap<>();
        }
        return kpis;
    }
}
