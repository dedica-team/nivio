package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.Source;

import java.util.*;

public final class LandscapeBuilder {
    private Set<Item> items = new HashSet<>();
    private Map<String, Link> links = new HashMap<>();
    private Map<String, KPI> kpis = new HashMap<>();
    private String identifier;
    private String name;
    private String contact;
    private String owner;
    private String description;
    private Source source;
    private LandscapeConfig config;
    private ProcessLog processLog;
    private Map<String, Group> groups = new HashMap<>();
    private Map<String, String> labels = new HashMap<>();

    private LandscapeBuilder() {
    }

    public static LandscapeBuilder aLandscape() {
        return new LandscapeBuilder();
    }

    public LandscapeBuilder withItems(Set<Item> items) {
        this.items = items;
        return this;
    }

    public LandscapeBuilder withLinks(Map<String, Link> links) {
        this.links = links;
        return this;
    }

    public LandscapeBuilder withKpis(Map<String, KPI> kpis) {
        this.kpis = kpis;
        return this;
    }

    public LandscapeBuilder withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public LandscapeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public LandscapeBuilder withContact(String contact) {
        this.contact = contact;
        return this;
    }

    public LandscapeBuilder withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public LandscapeBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public LandscapeBuilder withSource(Source source) {
        this.source = source;
        return this;
    }

    public LandscapeBuilder withConfig(LandscapeConfig config) {
        this.config = config;
        return this;
    }

    public LandscapeBuilder withProcessLog(ProcessLog processLog) {
        this.processLog = processLog;
        return this;
    }

    public LandscapeBuilder withGroups(Map<String, Group> groups) {
        this.groups = groups;
        return this;
    }

    public LandscapeBuilder withLabels(Map<String, String> labels) {
        this.labels = labels;
        return this;
    }

    public Landscape build() {
        Objects.requireNonNull(identifier, "The landscape identifier cannot be null");

        Landscape landscape = new Landscape(identifier, groups, name, contact, owner, description, source, config, processLog, kpis);
        landscape.setItems(items);
        landscape.setLinks(links);

        labels.forEach((s, s2) -> landscape.getLabels().put(s, s2));
        return landscape;
    }

}
