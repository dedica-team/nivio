package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.landscape.LandscapeItem;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;

@Component
public class FileChangeProcessor implements ApplicationListener<FSChangeEvent> {

    private final Indexer indexer;
    private final FileFetcher fileFetcher;

    @Autowired
    public FileChangeProcessor(Indexer indexer, FileFetcher fileFetcher) {
        this.indexer = indexer;
        this.fileFetcher = fileFetcher;
    }

    @Override
    public void onApplicationEvent(FSChangeEvent fsChangeEvent) {
        String s = (fsChangeEvent.getEvent().context()).toString();
        process(new File(fsChangeEvent.getPath() + "/" + s));
    }

    public ProcessLog process(File envFile) {
        Environment environment = EnvironmentFactory.fromYaml(envFile);
        return process(environment);
    }

    public ProcessLog process(Environment environment) {
        return indexer.reIndex(environment);
    }
}
