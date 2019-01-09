package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.landscape.LandscapeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    public ProcessLog process(LandscapeItem item) {
        if (item == null || StringUtils.isEmpty(item.getSource())) {
            return new ProcessLog(new ProcessingException(item, "Cannot process empty source."));
        }

        File file = new File(item.getSource());
        if (file.exists())
            return process(file);

        return process(EnvironmentFactory.fromString(item.getSource()));
    }

    public ProcessLog process(Environment environment) {
        return indexer.reIndex(environment);
    }
}
