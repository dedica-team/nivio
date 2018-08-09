package de.bonndan.nivio;

import de.bonndan.nivio.input.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = "de.bonndan.nivio.landscape")
@EnableAsync
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

    //TODO remove after alpha phase
    @Bean
    CommandLineRunner demo(FileChangeProcessor processor) {
        return args -> {
            Path currentRelativePath = Paths.get("");
            File file = new File(currentRelativePath.toAbsolutePath().toString() + "/src/test/resources/example/example_env.yml");
            processor.process(file);
        };
    }


    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
        taskExecutor.execute(context.getBean(DirectoryWatcher.class));
    }

}
