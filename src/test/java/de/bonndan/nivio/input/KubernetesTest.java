package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.kubernetes.InputFormatHandlerKubernetes;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodStatusBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EnableKubernetesMockClient(crud = true, https = false)
public class KubernetesTest {

    static KubernetesClient client;

    @BeforeEach
    void setup() {
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
    public void testRead() {

        SourceReference sourceReference = new SourceReference(null, "k8s");
        sourceReference.setUrl("http://localhost:80?groupLabel=release&namespace=default");

        InputFormatHandlerKubernetes factory = new InputFormatHandlerKubernetes(java.util.Optional.ofNullable(client));
        factory.getConfiguration().setNamespace("default");

        LandscapeDescription landscapeDescription = new LandscapeDescription("test");

        //when
        factory.applyData(sourceReference, null, landscapeDescription);

        //then
        assertEquals(3, landscapeDescription.getItemDescriptions().all().size());

        ItemDescription itemDescription = landscapeDescription.getItemDescriptions().all().stream()
                .filter(itemDescription1 -> ItemType.POD.equals(itemDescription1.getType()))
                .findFirst()
                .get();
        assertNotNull(itemDescription);

        assertEquals("testgroup", itemDescription.getGroup());
        assertEquals("pod1", itemDescription.getName());
        assertEquals("pod1", itemDescription.getIdentifier());
        assertEquals("testgroup", itemDescription.getLabels().get("release"));
    }
}
