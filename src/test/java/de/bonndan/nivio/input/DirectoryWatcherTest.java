package de.bonndan.nivio.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static de.bonndan.nivio.input.DirectoryWatcher.getExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class DirectoryWatcherTest {

    private ApplicationEventPublisher publisher;

    @BeforeEach
    public void setup() {
        publisher = Mockito.mock(ApplicationEventPublisher.class);
    }

    @Test
    public void testFileChange() throws IOException, InterruptedException, TimeoutException, ExecutionException {
        File tempFile = File.createTempFile("test", "." + DirectoryWatcher.SUFFIX_WHITELIST.get(0), new File(System.getProperty("java.io.tmpdir")));
        DirectoryWatcher directoryWatcher = new DirectoryWatcher(publisher, tempFile);
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.initialize();
        Future<?> submit = threadPoolExecutor.submit(directoryWatcher);

        Thread.sleep(1000);
        Files.write(tempFile.toPath(), "Hallo".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        Thread.sleep(1000);

        ArgumentCaptor<FSChangeEvent> captor = ArgumentCaptor.forClass(FSChangeEvent.class);
        verify(publisher).publishEvent(captor.capture());

        submit.cancel(true);
    }

    @Test
    public void testFileChangeIgnored() throws IOException, InterruptedException, TimeoutException, ExecutionException {
        File tempFile = File.createTempFile("test", ".png", new File(System.getProperty("java.io.tmpdir")));
        DirectoryWatcher directoryWatcher = new DirectoryWatcher(publisher, tempFile);
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.initialize();
        Future<?> submit = threadPoolExecutor.submit(directoryWatcher);

        Thread.sleep(1000);
        Files.write(tempFile.toPath(), "Hallo".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        Thread.sleep(1000);

        ArgumentCaptor<FSChangeEvent> captor = ArgumentCaptor.forClass(FSChangeEvent.class);
        verify(publisher, never()).publishEvent(captor.capture());

        submit.cancel(true);
    }

    /**
     * copied from https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
     */
    @Test
    public void testGetExtension() {
        assertEquals("", getExtension("C"));
        assertEquals("ext", getExtension("C.ext"));
        assertEquals("ext", getExtension("A/B/C.ext"));
        assertEquals("", getExtension("A/B/C.ext/"));
        assertEquals("", getExtension("A/B/C.ext/.."));
        assertEquals("bin", getExtension("A/B/C.bin"));
        assertEquals("hidden", getExtension(".hidden"));
        assertEquals("dsstore", getExtension("/user/home/.dsstore"));
        assertEquals("", getExtension(".strange."));
        assertEquals("3", getExtension("1.2.3"));
        assertEquals("exe", getExtension("C:\\Program Files (x86)\\java\\bin\\javaw.exe"));
    }
}