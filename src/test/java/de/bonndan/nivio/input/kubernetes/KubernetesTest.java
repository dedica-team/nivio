package de.bonndan.nivio.input.kubernetes;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodStatusBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EnableKubernetesMockClient(crud = true)
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
}
