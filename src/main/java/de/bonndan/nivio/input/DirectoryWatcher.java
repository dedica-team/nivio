package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

public class DirectoryWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);

    /**
     * List of file extensions which can trigger events. Empty extension is allowed.
     */
    public static final List<String> SUFFIX_WHITELIST = Arrays.asList("", "yaml", "yml", "txt");

    private final ApplicationEventPublisher publisher;
    private final File file;

    public DirectoryWatcher(ApplicationEventPublisher publisher, File file) {
        this.publisher = publisher;
        this.file = file;
    }

    public void run() {
        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            logger.error("Could not create new watchservice", e);
            throw new ProcessingException("Could not create new watchservice", e);
        }

        Path path = file.isDirectory() ? Paths.get(file.getPath()) : Paths.get(file.getParent());

        try {
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            logger.error("Could not create new watchservice", e);
            throw new ProcessingException("Could not create new watchservice for " + path, e);
        }

        WatchKey key;
        logger.info("Starting directory watcher on " + path.toAbsolutePath());
        try {
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {

                    FSChangeEvent fsChangeEvent = new FSChangeEvent(this, event, path);
                    File changedFile = fsChangeEvent.getChangedFile();
                    if (changedFile.isFile()) {
                        String extension = getExtension(changedFile.getName());
                        if (!SUFFIX_WHITELIST.contains(extension)) {
                            logger.info("Ignoring file {} because of extension {}", changedFile, extension);
                            continue;
                        }
                    }
                    publisher.publishEvent(fsChangeEvent);
                    logger.info("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            logger.warn("Directory watcher was interrupted");
        }
    }

    //https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
    public static String getExtension(String fileName) {
        char ch;
        int len;
        if(fileName==null ||
                (len = fileName.length())==0 ||
                (ch = fileName.charAt(len-1))=='/' || ch=='\\' || //in the case of a directory
                ch=='.' ) //in the case of . or ..
            return "";
        int dotInd = fileName.lastIndexOf('.'),
                sepInd = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if( dotInd<=sepInd )
            return "";
        else
            return fileName.substring(dotInd+1).toLowerCase();
    }
}
