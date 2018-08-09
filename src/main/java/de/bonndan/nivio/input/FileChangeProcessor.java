package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.output.GraphBuilder;
import de.bonndan.nivio.output.dld4e.Dld4eRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

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
        process(new File(DirectoryWatcher.NIVIO_ENV_DIRECTORY + "/" + s));
    }

    public void process(File envFile) {
        try {
            Environment environment = EnvironmentFactory.fromYaml(envFile);

            Landscape landscape = indexer.reIndex(environment);
            logger.info("Rendering graph for landscape " + landscape.getPath());
            Dld4eRenderer graphRenderer = new Dld4eRenderer();
            File rendered = new File("/tmp/"+landscape.getIdentifier().replaceAll("[^a-zA-Z0-9-_\\.]", "_")+".yml");
            graphRenderer.render(landscape, rendered);
            logger.info("Exported " + rendered.getAbsolutePath());


        } catch (ReadingException e) {
            logger.error("Failed to read " + envFile, e);
        } catch (IOException e) {
            logger.error("Failed to write output file", e);
        }
    }
}
