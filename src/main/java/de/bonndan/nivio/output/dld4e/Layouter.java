package de.bonndan.nivio.output.dld4e;

import static de.bonndan.nivio.model.RelationType.PROVIDER;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Arranges icons per group.
 */
public class Layouter {

    public static final int APPLICATION_LEVEL = 10;
    public static final int GROUP_PROVIDER_LEVEL = 6;
    public static final int COMMON_PROVIDER_LEVEL = 2;

    final int[] xServices = {0};
    final int[] xProviders = {0};
    final int[] xCommonProviders = {0};

    public void arrange(final Icons icons, final Groups groups) {


        groups.getAll().forEach((name, serviceList) -> {
            serviceList.forEach(service -> {
                icons.by(service).ifPresent(icon1 -> {
                    if (service.getRelations(PROVIDER).isEmpty()) { //application level
                        icon1.set("x", xServices[0]).set("y", APPLICATION_LEVEL);
                        xServices[0]++;
                    } else {
                        icon1.set("x", xProviders[0]).set("y", GROUP_PROVIDER_LEVEL);
                        xProviders[0]++;
                    }
                });
            });

            //second run for common / ungrouped infrastructure
            serviceList.forEach(service -> {
                icons.by(service).ifPresent(icon1 -> {
                    if (!service.getProvidedBy().isEmpty()) { //application level
                        service.getProvidedBy().forEach(provider -> {
                            icons.by(provider).ifPresent(icon -> {
                                        if (isEmpty(provider.getGroup())) {
                                            icon.set("x", xCommonProviders[0]).set("y", COMMON_PROVIDER_LEVEL);
                                            xCommonProviders[0]++;
                                        }
                                    }

                            );
                        });
                    }
                });
            });
            xServices[0]++;
            xProviders[0] = xServices[0];
        });
    }

    /**
     * Calculates the number of columns based on the icons arranged in groups.
     *
     * @return number of columns in diagram plus 1 padding
     */
    public int getColumns() {
        return Math.max(xServices[0], Math.max(xCommonProviders[0], xProviders[0]) + 2);
    }
}
