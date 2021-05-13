package de.bonndan.nivio.output.docs;

import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class SearchConfig {

    private String searchTerm;

    public SearchConfig(@NonNull final Map<String, String[]> parameterMap) {
        Map<String, String[]> map = Objects.requireNonNullElse(parameterMap, new HashMap<>());
        if (map.containsKey("searchTerm")) {
            this.searchTerm = map.get("searchTerm")[0];
        }
    }

    public String getSearchTerm() {
        return searchTerm;
    }
}
