package de.bonndan.nivio.input.rancher1.patches;

import io.rancher.base.AbstractType;
import io.rancher.type.ToServiceUpgradeStrategy;

public class ServiceUpgrade extends AbstractType {
    private InServiceUpgradeStrategy inServiceStrategy;
    private ToServiceUpgradeStrategy toServiceStrategy;

    public ServiceUpgrade() {
    }

    public InServiceUpgradeStrategy getInServiceStrategy() {
        return this.inServiceStrategy;
    }

    public void setInServiceStrategy(InServiceUpgradeStrategy inServiceStrategy) {
        this.inServiceStrategy = inServiceStrategy;
    }

    public ToServiceUpgradeStrategy getToServiceStrategy() {
        return this.toServiceStrategy;
    }

    public void setToServiceStrategy(ToServiceUpgradeStrategy toServiceStrategy) {
        this.toServiceStrategy = toServiceStrategy;
    }
}
