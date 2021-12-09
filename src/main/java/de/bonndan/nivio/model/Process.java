package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Process implements Linked, Tagged, Labeled, Assessable {

    private final Map<String, Link> links = new HashMap<>();
    private final Map<String, String> labels = new HashMap<>();

    @Override
    public Set<StatusValue> getAdditionalStatusValues() {
        return null;
    }

    @Override
    public String getAssessmentIdentifier() {
        return null;
    }

    @Override
    public List<? extends Assessable> getChildren() {
        return null;
    }

    @Override
    public String getLabel(String key) {
        return null;
    }

    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    @Override
    public void setLabel(String key, String value) {
        labels.put(key, value);
    }

    @Override
    public Map<String, Link> getLinks() {
        return links;
    }
}
