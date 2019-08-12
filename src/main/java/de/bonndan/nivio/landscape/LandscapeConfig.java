package de.bonndan.nivio.landscape;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LandscapeConfig {

    private Map<String, GroupConfig> groups = new HashMap<>();

    public Optional<GroupConfig> getGroupConfig(String group) {
        return Optional.ofNullable(groups.get(group));
    }

    public Map<String, GroupConfig> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, GroupConfig> groups) {
        this.groups = groups;
    }

    public static class GroupConfig {
        private String color;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}
