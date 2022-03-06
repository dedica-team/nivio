package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.*;

import static de.bonndan.nivio.util.SafeAssign.assignSafe;
import static de.bonndan.nivio.util.SafeAssign.assignSafeIfAbsent;

/**
 * This is representation of a service in the textual form as described in a source file.
 */
public class ItemDescription extends ComponentDescription implements Tagged, ItemComponent {

    @Schema(description = "The identifier of the group this item belongs in. Every item requires to be member of a group internally, so if nothing is given, the value is set to its layer.",
            example = "shipping")
    private String group;

    @Schema(description = "The technical layer", example = "infrastructure")
    private String layer;

    @Schema(description = "A collection of low level interfaces. Can be used to describe HTTP API endpoints for instance.")
    @JsonDeserialize(contentAs = InterfaceDescription.class)
    private Set<InterfaceDescription> interfaces = new HashSet<>();

    @Schema(description = "A collection of identifiers which are providers for this item (i.e. hard dependencies that are required). This is a convenience field to build relations.", example = "shipping-mysqldb")
    private List<String> providedBy = new ArrayList<>();

    @Schema(description = "Subcomponents")
    private Set<PartDescription> parts = new HashSet<>();

    @Schema(description = "The technical address of the item (should be an URI). Taken into account when matching relation endpoints.")
    private String address;

    @Schema(description = "Helper field to assign items to landscapes when unclear", hidden = true)
    @NotEmpty
    private String landscape;

    public ItemDescription(String identifier) {
        super();
        this.setIdentifier(identifier);
    }

    public ItemDescription() {

    }

    /**
     * Copies the values of the template where own value is null.
     *
     * @param template source
     */
    public void assignFromTemplate(@NonNull final ItemDescription template) {

        if (Objects.requireNonNull(template) == this) {
            return;
        }

        super.assignSafeNotNull(template);

        assignSafeIfAbsent(template.getGroup(), getGroup(), this::setGroup);
        assignSafeIfAbsent(template.getIcon(), getIcon(), this::setIcon);
        assignSafeIfAbsent(template.getAddress(), getAddress(), this::setAddress);
        assignSafeIfAbsent(template.getLayer(), getLayer(), this::setLayer);

        if (template.getProvidedBy() != null) {
            template.getProvidedBy().stream()
                    .filter(s -> StringUtils.hasLength(s) && !this.getProvidedBy().contains(s))
                    .forEach(s -> this.getProvidedBy().add(s));
        }

        template.getRelations().forEach(this::addOrReplaceRelation);

        getInterfaces().addAll(template.getInterfaces());
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Schema(description = "The lifecycle state of an item.", allowableValues = {"PLANNED", "INTEGRATION", "TEST", "PRODUCTION", "END_OF_LIFE", "EOL"})
    public void setLifecycle(String lifecycle) {

        //try to standardize using enum values
        if (StringUtils.hasLength(lifecycle)) {
            Lifecycle from = Lifecycle.from(lifecycle);
            if (from != null) {
                lifecycle = from.name();
            }
        }

        if (lifecycle != null) {
            this.setLabel(Label.lifecycle, lifecycle);
        }
    }

    public Set<InterfaceDescription> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Set<InterfaceDescription> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * Syntactic sugar to create relations from providers.
     *
     * @return provider identifier
     */
    public List<String> getProvidedBy() {
        return providedBy;
    }

    public void setProvidedBy(List<String> providedBy) {
        this.providedBy = providedBy;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Legacy setter for {@link StatusValue}.
     *
     * @param status a list of key-value pairs, keys are "label", "status", "message"
     */
    @Schema(name = "statuses", description = "A list of statuses that works like hardcoded KPIs.")
    public void setStatuses(List<LinkedHashMap<String, String>> status) {
        setStatus(status);
    }

    /**
     * Legacy setter for {@link StatusValue}.
     *
     * @param status a list of key-value pairs, keys are "label", "status", "message"
     */
    @Schema(name = "status", description = "A list of statuses that works like hardcoded KPIs.")
    public void setStatus(List<LinkedHashMap<String, String>> status) {
        status.forEach(map -> {
            String key = map.get("label");
            if (key != null) {
                String value = map.get(StatusValue.LABEL_SUFFIX_STATUS);
                String message = map.get(StatusValue.LABEL_SUFFIX_MESSAGE);
                setLabel(Label.withPrefix(Label.status, key, StatusValue.LABEL_SUFFIX_STATUS), value);
                setLabel(Label.withPrefix(Label.status, key, StatusValue.LABEL_SUFFIX_MESSAGE), message);
            }
        });
    }

    /**
     * Setter for framework map.
     *
     * @param frameworks "name": "version"
     * @see Label
     */
    @Schema(description = "The parts used to create the item. Usually refers to technical frameworks.", type = "Map", example = "java: 8")
    public void setFrameworks(final Map<String, String> frameworks) {
        frameworks.forEach(this::setFramework);
    }

    public void setFramework(@NonNull final String key, String value) {
        setLabel(Label.framework.withPrefix(key), value);
    }

    @Override
    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public void assignNotNull(ItemDescription increment) {
        super.assignNotNull(increment);
        if (increment.getGroup() != null) {
            setGroup(increment.getGroup());
        }
        if (increment.getAddress() != null) {
            setAddress(increment.getAddress());
        }
        if (increment.getLayer() != null) {
            setLayer(increment.getLayer());
        }

        if (increment.getProvidedBy() != null) {
            increment.getProvidedBy().stream()
                    .filter(s -> StringUtils.hasLength(s) && !this.getProvidedBy().contains(s))
                    .forEach(s -> this.getProvidedBy().add(s));
        }

        if (increment.getParts() != null) {
            increment.getParts().forEach(this::addOrReplacePart);
        }

        increment.getRelations().forEach(this::addOrReplaceRelation);

        assignSafe(increment.getInterfaces(), set -> set.forEach(intf -> getInterfaces().add(intf)));
    }

    private void addOrReplacePart(PartDescription partDescription) {
        parts.remove(partDescription);
        parts.add(partDescription);
    }

    @NonNull
    @Override
    public URI getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.forDescription(ItemDescription.class,  null, null, null, group, getIdentifier(),  null);
    }

    @Override
    public String getParentIdentifier() {
        return getGroup();
    }

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    public Set<PartDescription> getParts() {
        return parts;
    }

    public void setParts(Set<PartDescription> parts) {
        this.parts = parts;
    }
}
