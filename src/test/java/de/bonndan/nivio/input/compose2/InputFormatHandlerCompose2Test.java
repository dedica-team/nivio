package de.bonndan.nivio.input.compose2;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.search.ItemIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InputFormatHandlerCompose2Test {

    private FileFetcher fileFetcher;

    @BeforeEach
    public void setup() {
        fileFetcher = new FileFetcher(new HttpService());
    }

    @Test
    public void readCompose() {
        SourceReference file = new SourceReference(new File(getRootPath() + "/src/test/resources/example/services/docker-compose.yml").toURI().toString());
        InputFormatHandlerCompose2 factoryCompose2 = new InputFormatHandlerCompose2(fileFetcher);
        LandscapeDescription landscapeDescription = new LandscapeDescription("test");

        //when
       factoryCompose2.applyData(file, null, landscapeDescription);

        //then
        ItemIndex<ItemDescription> services = landscapeDescription.getItemDescriptions();
        assertEquals(3, services.all().size());
        ItemDescription service = services.pick("web", null);
        assertNotNull(service);

        assertEquals("web", service.getIdentifier());
        assertNotNull(service.getLabels(Label.network));
        assertEquals(2, service.getLabels(Label.network).size());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
