package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.util.URLHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.http.Url;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileSourceReferenceObserverTest {

    private FileFetcher fileFetcher;
    private LandscapeDescriptionFactory landscapeDescriptionFactory;

    @BeforeEach
    public void setup() {
        fileFetcher = new FileFetcher(new HttpService());
        landscapeDescriptionFactory = new LandscapeDescriptionFactory(fileFetcher);
    }

    @Test
    public void canHandleCombinedPath() throws IOException, ExecutionException, InterruptedException {

        String realpath = getRootPath() + "/src/test/resources/example/services/wordpress.yml";
        File realfile = new File(realpath);

        String source = getRootPath() + "/src/test/resources/example/example_env.yml";
        File file = new File(source);

        LandscapeDescription description = landscapeDescriptionFactory.fromYaml(file);

        SourceReference reference = description.getSourceReferences().get(0);
        reference.setUrl("./services/wordpress.yml");

        FileFetcher mocked = mock(FileFetcher.class);
        when(mocked.get(any(SourceReference.class), any(URL.class))).thenAnswer(invocationOnMock -> String.valueOf(Math.random()));

        FileSourceReferenceObserver observer = new FileSourceReferenceObserver(
                mocked,
                reference,
                URLHelper.getParentPath(description.getSource()).orElse(null)
        );

        //when
        String s = observer.hasChange().get();
        assertTrue(s.contains("src/test/resources/example/services/wordpress.yml"));
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}