package de.bonndan.nivio.output.layout;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LayoutConfig {

    /**
     * This could be made configurable, but for now there is only one layouter implementation.
     *
     * @return the organic layouter
     */
    @Bean
    public Layouter getLayouter() {
        return new OrganicLayouter();
    }
}
