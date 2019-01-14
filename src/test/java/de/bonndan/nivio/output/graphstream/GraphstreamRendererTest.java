package de.bonndan.nivio.output.graphstream;

import de.bonndan.nivio.landscape.Landscape;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class GraphstreamRendererTest {

    @Test
    public void dump() throws IOException {
        Landscape landscape = new Landscape();
        landscape.setIdentifier("x");
        landscape.setName("x");

        GraphStreamRenderer graphStreamRenderer = new GraphStreamRenderer();
        graphStreamRenderer.render(landscape, new File(""));
        String dump = graphStreamRenderer.getGraphDump();
        assertTrue(!StringUtils.isEmpty(dump));
    }
}
