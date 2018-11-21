package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.landscape.Landscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileChangeProcessor implements ApplicationListener<FSChangeEvent> {

    private static final Logger logger = LoggerFactory.getLogger(FileChangeProcessor.class);

    private final Indexer indexer;

    @Autowired
    public FileChangeProcessor(Indexer indexer) {
        this.indexer = indexer;
    }

    @Override
    public void onApplicationEvent(FSChangeEvent fsChangeEvent) {
        String s = (fsChangeEvent.getEvent().context()).toString();
        process(new File(fsChangeEvent.getPath() + "/" + s));
    }

    public void process(File envFile) {
            Environment environment = EnvironmentFactory.fromYaml(envFile);
            process(environment);

    }

    public Landscape process(Environment environment) {
        try {
            Landscape landscape = indexer.reIndex(environment);
            logger.info("Reindexed landscape " + environment.getIdentifier());
            return landscape;

        } catch (ReadingException e) {
            logger.error("Failed to read " + environment, e);
        }
        return null;
    }
}
