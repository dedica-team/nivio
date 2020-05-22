package de.bonndan.nivio.input;

import de.bonndan.nivio.IndexEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.LandscapeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class handles local file changes to tries to find the landscape belonging to the file, then trigger an indexing.
 *
 *
 */
@Component
public class FileChangeProcessor implements ApplicationListener<FSChangeEvent> {

    private final LandscapeRepository landscapeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public FileChangeProcessor(LandscapeRepository landscapeRepository,
                               ApplicationEventPublisher applicationEventPublisher
    ) {
        this.landscapeRepository = landscapeRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onApplicationEvent(FSChangeEvent fsChangeEvent) {

        File changedFile = fsChangeEvent.getChangedFile();
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(changedFile);
        if (landscapeDescription == null) {
            new ProcessLog(new ProcessingException("Could not read environment from " + changedFile, new RuntimeException()));
            return;
        }

        //this is not an environment file, but likely a service description file
        if (landscapeDescription.getIdentifier() == null && !StringUtils.isEmpty(landscapeDescription.getSource())) {
            if (handleServiceDescriptionFileChange(landscapeDescription, changedFile)) {
                return;
            }
        }
        process(landscapeDescription, changedFile);
    }

    private boolean handleServiceDescriptionFileChange(LandscapeDescription landscapeDescription,
                                                                    File changedFile
    ) {
        AtomicBoolean process = new AtomicBoolean(false);
        landscapeRepository.findAll().forEach(landscape -> {
            LandscapeDescription env1 = LandscapeDescriptionFactory.fromYaml(new File(landscape.getSource()));
            if (env1 != null && env1.hasReference(landscapeDescription.getSource())) {
                process(env1, changedFile);
                process.set(true);
            }
        });
        return process.get();
    }

    private void process(LandscapeDescription landscapeDescription, File changedFile) {
        applicationEventPublisher.publishEvent(new IndexEvent(this, landscapeDescription, "File change: " + changedFile));
    }
}
