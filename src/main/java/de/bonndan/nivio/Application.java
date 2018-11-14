package de.bonndan.nivio;

import de.bonndan.nivio.input.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.MalformedURLException;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = "de.bonndan.nivio.landscape")
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
    public Seed seed() {
        return new Seed();
    }

    public static void main(String[] args) throws MalformedURLException {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        Seed seed = (Seed) context.getBean("seed");
        if (!StringUtils.isEmpty(seed.getSeed())) {
            ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
            WatcherFactory watcher = context.getBean(WatcherFactory.class);
            watcher.getWatchers().forEach(taskExecutor::execute);

        //demo mode
        } else if (!StringUtils.isEmpty(System.getenv(Seed.DEMO))) {
            log.info("Running in demo mode");

            FileChangeProcessor processor = context.getBean(FileChangeProcessor.class);
            processor.process(Seed.getDemoFile());

            ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
            WatcherFactory watcher = context.getBean(WatcherFactory.class);
            taskExecutor.execute(watcher.getWatcher(Seed.getDemoFile()));
        }
    }

}
