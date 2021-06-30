package de.bonndan.nivio.output.docs;

import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class SearchConfig {

    private String searchTerm;
    private String title;

    public SearchConfig(@NonNull final Map<String, String[]> parameterMap) {
        Map<String, String[]> map = Objects.requireNonNullElse(parameterMap, new HashMap<>());
        if (map.containsKey("searchTerm")) {
            this.searchTerm = map.get("searchTerm")[0];
        }
        if (map.containsKey("title")) {
            this.title = map.get("title")[0];
        }
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public String getTitle() {
        return title;
    }
}
