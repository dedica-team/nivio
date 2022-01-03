package de.bonndan.nivio.output.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FrontendMappingApiModel {

    private final Map<String, String> keys;

    private final Map<String, String> descriptions;

    public FrontendMappingApiModel(@Nullable final Map<String, String> keys,
                                   @Nullable final Map<String, String> descriptions
    ) {
        this.keys = keys == null ? new HashMap<>() : keys;
        this.descriptions = descriptions == null ? new HashMap<>() : descriptions;
    }

    @Schema(description = "Mapping of internally used keys or terms and their display value")
    public Map<String, String> getKeys() {
        return keys;
    }

    @Schema(description = "Dictionary of internally used keys or terms and their explanations")
    public Map<String, String> getDescriptions() {
        return descriptions;
    }
}
