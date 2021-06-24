package de.bonndan.nivio.config;

import de.bonndan.nivio.input.demo.ChangeTrigger;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LazyConfig {

    /**
     * Excludes {@link ChangeTrigger} from lazy initialisation so that scheduled calling can work.
     */
    @Bean
    public LazyInitializationExcludeFilter lazyInitializationExcludeFilter() {
        return LazyInitializationExcludeFilter.forBeanTypes(ChangeTrigger.class);
    }
}
