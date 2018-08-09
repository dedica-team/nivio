package de.bonndan.nivio.output.dld4e;

import java.util.Optional;

/**
 * Arranges icons per group.
 *
 *
 *
 */
public class Layouter {
    public void arrange(Icons icons, Groups groups) {

        final int[] xServices = {1};
        final int[] xProviders = {1};
        groups.getAll().forEach((name, serviceList) -> {
            serviceList.forEach(service -> {
                Optional<Icon> icon = icons.by(service);
                if (icon.isPresent()) {
                    icon.get()
                            .set("x", xServices[0])
                            .set("y", 1);
                    xServices[0]++;
                }

                service.getProvidedBy().forEach(provider -> {
                    Optional<Icon> providerIcon = icons.by(provider);

                    //set position of the provider only once
                    if (providerIcon.isPresent() && providerIcon.get().get("x") == null) {
                        providerIcon.get()
                                .set("x", xProviders[0])
                                .set("y", 2);
                        xProviders[0]++;
                    }
                });
            });
            xServices[0]++;
        });
    }
}
