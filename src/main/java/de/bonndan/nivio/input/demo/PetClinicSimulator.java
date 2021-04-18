package de.bonndan.nivio.input.demo;

import com.github.jknack.handlebars.internal.Files;
import de.bonndan.nivio.config.ConfigurableEnvVars;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Label;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Simulates a pet clinic landscape in a kubernetes context.
 */
@Component
public class PetClinicSimulator {

    private static final String DEMODATA = "demodata.";
    private static final Logger LOGGER = LoggerFactory.getLogger(PetClinicSimulator.class);

    private final KubernetesClient client;
    private final LandscapeDescriptionFactory factory;
    private final List<Pod> pods = new ArrayList<>();

    public PetClinicSimulator(final LandscapeDescriptionFactory factory) {
        this.factory = factory;

        if (ConfigurableEnvVars.DEMO.value().isEmpty()) {
            LOGGER.debug("DEMO env var is not given, pet clinic simulation is off.");
            client = null;
            return;
        }

        KubernetesMockServer kubernetesMockServer = new KubernetesMockServer(false);
        LOGGER.info("Starting pet clinic simulation on port 8888");
        kubernetesMockServer.start(8888);
        client = kubernetesMockServer.createClient();
    }

    /**
     * Ramp up the k8s environment using input from a landscape description (only for convenience).
     */
    @PostConstruct
    public void init() {
        if (client == null) return;

        String yaml;
        LandscapeDescription petClinic;
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("pet_clinic_k8s.yml")) {
            if (resourceAsStream == null) {
                throw new RuntimeException("Could not load demo file pet_clinic_k8s.yml");
            }
            yaml = Files.read(resourceAsStream, StandardCharsets.UTF_8);
            petClinic = factory.fromString(yaml, "pet clinic demo");
        } catch (Exception e) {
            LOGGER.error("Failed to read pet clinic demo data", e);
            return;
        }

        //create a namespace for each group
        petClinic.getGroups().forEach((s, groupDescription) -> {
            Namespace ns = new Namespace();
            ns.setMetadata(new ObjectMetaBuilder().withName(groupDescription.getName()).build());
            client.namespaces().create(ns);
        });

        //create a pod for each item
        petClinic.getItemDescriptions().all().forEach(itemDescription -> {
            Pod pod = createPod(itemDescription);
            pods.add(client.pods().create(pod));
        });
    }


    public void simulateSomething() {
        if (client == null) return;

        Pod pod = pods.get(0);
        pod.getMetadata().getLabels().put(Label.version.name(), LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
    }

    private Pod createPod(ItemDescription itemDescription) {
        Container container = new Container();
        container.setName(UUID.randomUUID().toString());

        return new PodBuilder()
                .withNewMetadata()
                .withName(itemDescription.getName())
                .withNamespace(itemDescription.getGroup())
                .withLabels(itemDescription.getLabels())
                .endMetadata()
                .withSpec(
                        new PodSpecBuilder()
                                .withContainers(container)
                                .withNodeName(Optional.ofNullable(itemDescription.getLabel(DEMODATA + "node")).orElse("node1"))
                                .build()
                )
                .withStatus(new PodStatusBuilder().build())
                .build();
    }
}
