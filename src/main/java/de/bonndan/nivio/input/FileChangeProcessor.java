package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.LandscapeRepository;
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
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(envFile);
        if (landscapeDescription == null) {
            return new ProcessLog(new ProcessingException("Could not read environment from " + envFile, new RuntimeException()));
        }

        //this is not an environment file, but likely a service description file
        if (landscapeDescription.getIdentifier() == null && !StringUtils.isEmpty(landscapeDescription.getSource())) {
            return handleServiceDescriptionFileChange(landscapeDescription).orElse(
                    new ProcessLog(new ProcessingException("Could not read environment from " + envFile, new RuntimeException()))
            );
        }
        return process(landscapeDescription);
    }

    private Optional<ProcessLog> handleServiceDescriptionFileChange(LandscapeDescription landscapeDescription) {

        AtomicReference<ProcessLog> process = new AtomicReference<>();
        landscapeRepository.findAll().forEach(landscape -> {
            LandscapeDescription env1 = LandscapeDescriptionFactory.fromYaml(new File(landscape.getSource()));
            if (env1 != null && env1.hasReference(landscapeDescription.getSource())) {

                Optional.ofNullable(process(env1)).ifPresent(processLog -> {
                    process.set(processLog);
                    process.get().info("Reindexing triggered based on file change: " + landscapeDescription.getSource());
                });

            }
        });
        return Optional.of(process.get());
    }

    public ProcessLog process(LandscapeDescription landscapeDescription) {
        return indexer.reIndex(landscapeDescription);
    }
}
