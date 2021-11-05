package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Link;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

public abstract class ComponentApiModel {

    protected Map<String, Link> hateoasLinks = new HashMap<>();

    @Schema(name = "_links")
    @JsonProperty("_links")
    public Map<String, Link> getLinks() {
        return hateoasLinks;
    }

    public abstract Map<String, String> getLabels();

    public void setHateoasLinks(Map<String, Link> hateoasLinks) {
        this.hateoasLinks.putAll(hateoasLinks);
    }

    /**
     * Returns the labels without the internal ones (having prefixes).
     *
     * @return filtered labels
     */
    protected Map<String, String> getPublicLabels(Map<String, String> labels) {
        return Labeled.withoutKeys(labels, Label.INTERNAL_LABEL_PREFIX, Label.status.name());
    }

}
