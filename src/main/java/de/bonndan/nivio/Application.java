package de.bonndan.nivio;

import de.bonndan.nivio.input.*;
import de.bonndan.nivio.output.icons.VendorIcons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class Application {

    private final static Logger log = LoggerFactory.getLogger(Application.class);

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }

    @Bean
    public WebMvcConfigurer configurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

    @Bean
    public Seed seed() {
        return new Seed();
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        Seed seed = (Seed) context.getBean("seed");
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
        if (seed.hasValue()) {
            WatcherFactory watcher = context.getBean(WatcherFactory.class);
            watcher.getWatchers().forEach(taskExecutor::execute);

            //demo mode
        } else if (!StringUtils.isEmpty(System.getenv(Seed.DEMO))) {
            log.info("Running in demo mode");

            FileChangeProcessor processor = context.getBean(FileChangeProcessor.class);
            Seed.getDemoFiles().forEach(processor::process);

            WatcherFactory watcher = context.getBean(WatcherFactory.class);
            Seed.getDemoFiles().forEach(file -> taskExecutor.execute(watcher.getWatcher(file)));
        }
    }

}
