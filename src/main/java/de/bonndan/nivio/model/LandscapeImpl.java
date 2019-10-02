package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Pattern;
import java.util.*;

/**
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 *
 */
public class LandscapeImpl implements Landscape {

    /**
     * Immutable unique identifier. Maybe use an URN.
     */
    @Pattern(regexp = LandscapeItem.IDENTIFIER_VALIDATION)
    private String identifier;

    /**
     * Human readable name.
     */
    private String name;

    /**
     * Maintainer email
     */
    private String contact;

    private String source;

    @JsonManagedReference
    private Set<Item> items = new HashSet<>();

    private List<StateProviderConfig> stateProviders = new ArrayList<>();

    private LandscapeConfig config;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = StringUtils.trimAllWhitespace(identifier);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getContact() {
        return contact;
    }

    @Override
    public List<StateProviderConfig> getStateProviders() {
        return stateProviders;
    }

    @Override
    public LandscapeConfig getConfig() {
        return config == null ? new LandscapeConfig() : config;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void addItem(Item item) {
        item.setLandscape(this);
        item.getProvidedBy().forEach(s -> s.setLandscape(this));
        items.add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LandscapeImpl landscape = (LandscapeImpl) o;

        return StringUtils.trimAllWhitespace(identifier).equals(StringUtils.trimAllWhitespace(landscape.identifier));
    }

    @Override
    public int hashCode() {
        return Objects.hash(StringUtils.trimAllWhitespace(identifier));
    }

    public void setStateProviders(List<StateProviderConfig> stateProviders) {
        this.stateProviders = stateProviders;
    }

    public void setConfig(LandscapeConfig config) {
        this.config = config;
    }
}
