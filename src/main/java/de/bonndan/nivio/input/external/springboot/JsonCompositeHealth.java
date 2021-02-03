package de.bonndan.nivio.input.external.springboot;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * Unfortunately {@link org.springframework.boot.actuate.health.CompositeHealth} has no public constructor.
 */
@JsonDeserialize(builder = JsonCompositeHealth.Builder.class)
class JsonCompositeHealth implements Serializable {

    private final String status;

    private final Map<String, JsonCompositeHealth> components;

    private final Map<String, String> details;

    public JsonCompositeHealth(String status,
                               Map<String, JsonCompositeHealth> components,
                               Map<String, String> details
    ) {
        this.status = status;
        this.components = components;
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, JsonCompositeHealth> getComponents() {
        return components;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String status;
        private Map<String, JsonCompositeHealth> components;
        private Map<String, String> details;

        private Builder() {
        }

        public static Builder aJsonCompositeHealth() {
            return new Builder();
        }

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withComponents(Map<String, JsonCompositeHealth> components) {
            this.components = components;
            return this;
        }

        public Builder withDetails(Map<String, String> details) {
            this.details = details;
            return this;
        }

        public JsonCompositeHealth build() {
            return new JsonCompositeHealth(status, components, details);
        }
    }
}
