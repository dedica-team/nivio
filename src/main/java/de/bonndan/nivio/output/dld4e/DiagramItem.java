package de.bonndan.nivio.output.dld4e;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DiagramItem {

    private String item;
    private boolean referable;
    private String merge;
    private Map<String, String> attributes = new HashMap<>();

    DiagramItem(final String item) {
        this.item = item;
    }

    /**
     * anon list item constructor
     */
    public DiagramItem() {

    }

    public DiagramItem referable() {
        referable = true;
        return this;
    }

    public DiagramItem merge(final String reference) {
        if (!StringUtils.isEmpty(reference))
            merge = reference;
        return this;
    }

    public DiagramItem attributes(final Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(item == null ? "" : item);
        sb.append(":");
        if (referable) {
            sb.append(" &").append(item);
        }
        sb.append("\n");
        if (merge != null) {
            sb.append("  <<: *").append(merge).append("\n");
        }
        attributes.forEach((s, s2) -> {
            sb.append("  ").append(s).append(": ");
            sb.append(s2).append("\n");
        });
        return sb.toString();
    }

    public DiagramItem set(String key, String value) {
        attributes.put(key, value);
        return this;
    }

    public DiagramItem set(String key, int value) {
        attributes.put(key, String.valueOf(value));
        return this;
    }

    /**
     * Returns an attribute value if present.
     *
     * @param key attribute name
     * @return value or null
     */
    public String get(String key) {
        return attributes.getOrDefault(key, null);
    }

    public String inline() {

        StringBuilder sb = new StringBuilder("{");
        if (merge != null) {
            sb.append(" <<: *").append(merge).append(",");
        }
        attributes.forEach((s, s2) -> {
            sb.append("  ").append(s).append(": ");
            sb.append(s2).append(",");
        });
        if (',' == sb.charAt(sb.length() - 1)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}\n");
        return sb.toString();
    }
}
