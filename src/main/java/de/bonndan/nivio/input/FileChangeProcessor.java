package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileChangeProcessor implements ApplicationListener<FSChangeEvent> {

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

    public ProcessLog process(File envFile) {
        Environment environment = EnvironmentFactory.fromYaml(envFile);
        return process(environment);
    }

    public ProcessLog process(Environment environment) {
        return indexer.reIndex(environment);
    }
}
