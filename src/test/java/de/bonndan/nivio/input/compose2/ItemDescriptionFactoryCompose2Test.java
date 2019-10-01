package de.bonndan.nivio.input.compose2;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemDescriptionFactoryCompose2Test {

    private FileFetcher fileFetcher;

    @BeforeEach
    public void setup() {
        fileFetcher = new FileFetcher(new HttpService());
    }

    @Test
    public void readCompose() {
        SourceReference file = new SourceReference(getRootPath() + "/src/test/resources/example/services/docker-compose.yml");
        String yml = fileFetcher.get(file);
        ItemDescriptionFactoryCompose2 factoryCompose2 = new ItemDescriptionFactoryCompose2(fileFetcher, null);
        List<ItemDescription> services = factoryCompose2.getDescriptions(file);
        assertEquals(3, services.size());
        ItemDescription service = services.get(0);
        assertNotNull(service);

        assertEquals("web", service.getIdentifier());
        assertNotNull(service.getNetworks());
        assertEquals(2, service.getNetworks().size());
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
