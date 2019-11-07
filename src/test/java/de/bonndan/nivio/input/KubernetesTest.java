package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceFormat;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.kubernetes.ItemDescriptionFactoryKubernetes;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KubernetesTest {

    private NamespacedKubernetesClient client;
    private KubernetesServer server;

    @BeforeEach
    void setup() {

        server = new KubernetesServer(false, true);
        server.before();

        client = server.getClient();

        List<Container> containers = new ArrayList<>();
        Container c1 = new Container();
        c1.setImage("postgres:9.5");
        c1.setName("mydb");
        containers.add(c1);

        client.pods().inNamespace("default").create(
                new PodBuilder()
                        .withNewMetadata()
                        .withName("pod1")
                        .withNamespace("default")
                        .withLabels(Map.of("release", "testgroup"))
                        .endMetadata()
                        .withSpec(
                                new PodSpecBuilder()
                                        .withContainers(containers)
                                        .withNodeName("node1")
                                        .build()
                        )
                        .build()
        );

    }

    @Test
    public void testRead() throws IOException {

        SourceReference sourceReference = new SourceReference();
        sourceReference.setFormat(SourceFormat.KUBERNETES);
        sourceReference.setUrl("http://localhost:80?groupLabel=release&namespace=default");

        ItemDescriptionFactoryKubernetes factory = new ItemDescriptionFactoryKubernetes(sourceReference, client);
        factory.getConfiguration().setNamespace("default");

        List<ItemDescription> itemDescriptions = factory.getDescriptions(sourceReference);
        assertNotNull(itemDescriptions);
        assertEquals(1, itemDescriptions.size());

        ItemDescription itemDescription = itemDescriptions.get(0);
        assertNotNull(itemDescription);

        assertEquals("testgroup", itemDescription.getGroup());
        assertEquals("mydb", itemDescription.getName());
        assertEquals("mydb", itemDescription.getIdentifier());
        assertEquals("postgres:9.5", itemDescription.getSoftware());
        assertEquals("node1", itemDescription.getMachine());
        assertEquals("testgroup", itemDescription.getLabels().get("release"));

        tearDown();
    }

    @AfterEach
    private void tearDown() {
        server.after();
    }


}
