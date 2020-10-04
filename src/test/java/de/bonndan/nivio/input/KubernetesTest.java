package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.kubernetes.InputFormatHandlerKubernetes;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodStatusBuilder;
import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        var pod = new PodBuilder()
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
                .withStatus(new PodStatusBuilder().build())
                .build();
        client.pods().inNamespace("default").create(pod);

    }

    @Test
    public void testRead() throws IOException {

        SourceReference sourceReference = new SourceReference(null, "k8s");
        sourceReference.setUrl("http://localhost:80?groupLabel=release&namespace=default");

        InputFormatHandlerKubernetes factory = new InputFormatHandlerKubernetes(java.util.Optional.ofNullable(client));

        factory.getConfiguration().setNamespace("default");

        List<ItemDescription> itemDescriptions = factory.getDescriptions(sourceReference, null);
        assertNotNull(itemDescriptions);
        assertEquals(3, itemDescriptions.size());

        ItemDescription itemDescription = itemDescriptions.stream().filter(itemDescription1 -> ItemType.POD.equals(itemDescription1.getType())).findFirst().get();
        assertNotNull(itemDescription);

        assertEquals("testgroup", itemDescription.getGroup());
        assertEquals("pod1", itemDescription.getName());
        assertEquals("pod1", itemDescription.getIdentifier());
        assertEquals("testgroup", itemDescription.getLabels().get("release"));

        tearDown();
    }

    @AfterEach
    private void tearDown() {
        server.after();
    }


}
