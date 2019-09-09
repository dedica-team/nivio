package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.landscape.LandscapeRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class FileChangeProcessor implements ApplicationListener<FSChangeEvent> {

    private final Indexer indexer;
    private final LandscapeRepository landscapeRepository;

    public FileChangeProcessor(Indexer indexer, LandscapeRepository landscapeRepository) {
        this.indexer = indexer;
        this.landscapeRepository = landscapeRepository;
    }

    @Override
    public void onApplicationEvent(FSChangeEvent fsChangeEvent) {
        process(fsChangeEvent.getChangedFile());
    }

    public ProcessLog process(File envFile) {
        Environment environment = EnvironmentFactory.fromYaml(envFile);
        if (environment == null) {
            return new ProcessLog(new ProcessingException("Could not read environment from " + envFile, new RuntimeException()));
        }

        //this is not an environment file, but likely a service description file
        if (environment.getIdentifier() == null && !StringUtils.isEmpty(environment.getSource())) {
            return handleServiceDescriptionFileChange(environment).orElse(
                    new ProcessLog(new ProcessingException("Could not read environment from " + envFile, new RuntimeException()))
            );
        }
        return process(environment);
    }

    private Optional<ProcessLog> handleServiceDescriptionFileChange(Environment environment) {

        AtomicReference<ProcessLog> process = new AtomicReference<>();
        landscapeRepository.findAll().forEach(landscape -> {
            Environment env1 = EnvironmentFactory.fromYaml(new File(landscape.getSource()));
            if (env1 != null && env1.hasReference(environment.getSource())) {

                Optional.ofNullable(process(env1)).ifPresent(processLog -> {
                    process.set(processLog);
                    process.get().info("Reindexing triggered based on file change: " + environment.getSource());
                });

            }
        });
        return Optional.of(process.get());
    }

    public ProcessLog process(Environment environment) {
        return indexer.reIndex(environment);
    }
}
