package de.bonndan.nivio.output.dld4e;

import de.bonndan.nivio.model.Item;

import java.util.Map;

public class IconFamily {

    public static class AzureEnterprise {

        private static final Map<String, String> types = Map.ofEntries(
                Map.entry("database", "databasegeneric"),
                Map.entry("application", "applicationgeneric"),
                Map.entry("webserver", "webserver"),
                Map.entry("cloud", "cloud"),
                Map.entry("loadbalancer", "loadbalancergeneric"),
                Map.entry("firewall", "firewall")
        );

        public static final String name = "azureEnterprise";

        public static String iconFor(Item item) {
            return types.getOrDefault(item.getType(), "servergeneric");
        }
    }

}
