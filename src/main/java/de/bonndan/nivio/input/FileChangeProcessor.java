package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.output.GraphBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileChangeProcessor implements ApplicationListener<FSChangeEvent> {

    private static final Logger logger = LoggerFactory.getLogger(FileChangeProcessor.class);

    final Indexer indexer;

    final GraphBuilder graphBuilder;

    @Autowired
    public FileChangeProcessor(Indexer indexer, GraphBuilder graphBuilder) {
        this.indexer = indexer;
        this.graphBuilder = graphBuilder;
    }

    @Override
    public void onApplicationEvent(FSChangeEvent fsChangeEvent) {
        String s = (fsChangeEvent.getEvent().context()).toString();
        try {
            Environment environment = EnvironmentFactory.fromYaml(
                    new File(DirectoryWatcher.NIVIO_ENV_DIRECTORY + "/" + s)
            );

            Landscape landscape = indexer.reIndex(environment);

        } catch (ReadingException e) {
            logger.error("Failed to read " + DirectoryWatcher.NIVIO_ENV_DIRECTORY + "/" + s, e);
        }
    }
}
