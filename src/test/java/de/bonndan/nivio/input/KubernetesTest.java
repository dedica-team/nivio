package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.SourceFormat;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.kubernetes.ServiceDescriptionFactoryKubernetes;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KubernetesTest {

    private NamespacedKubernetesClient client;
    private KubernetesServer server;

    //TODO check why @BeforeEach does not work
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

        setup();
        SourceReference sourceReference = new SourceReference();
        sourceReference.setFormat(SourceFormat.KUBERNETES);
        sourceReference.setUrl("http://localhost:80?groupLabel=release&namespace=default");

        ServiceDescriptionFactoryKubernetes factory = new ServiceDescriptionFactoryKubernetes(sourceReference, client);
        factory.getConfiguration().setNamespace("default");

        List<ServiceDescription> serviceDescriptions = factory.getDescriptions(sourceReference);
        assertNotNull(serviceDescriptions);
        assertEquals(1, serviceDescriptions.size());

        ServiceDescription serviceDescription = serviceDescriptions.get(0);
        assertNotNull(serviceDescription);

        assertEquals("testgroup", serviceDescription.getGroup());
        assertEquals("mydb", serviceDescription.getName());
        assertEquals("mydb", serviceDescription.getIdentifier());
        assertEquals("postgres:9.5", serviceDescription.getSoftware());
        assertEquals("node1", serviceDescription.getMachine());
        assertEquals("testgroup", serviceDescription.getLabels().get("release"));

        tearDown();
    }

    @AfterEach
    private void tearDown() {
        server.after();
    }


}
