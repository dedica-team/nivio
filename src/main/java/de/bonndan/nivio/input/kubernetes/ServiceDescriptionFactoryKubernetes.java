package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ServiceDescriptionFactory;
import de.bonndan.nivio.input.dto.ServiceDescription;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.ArrayList;
import java.util.List;

public class ServiceDescriptionFactoryKubernetes implements ServiceDescriptionFactory {

    private KubernetesClient client;

    public ServiceDescriptionFactoryKubernetes() {

    }

    public ServiceDescriptionFactoryKubernetes(KubernetesClient client) {
        this.client = client;
    }


    @Override
    public List<ServiceDescription> fromString(String context) {
        KubernetesClient client = getClient(context);

        List<ServiceDescription> descriptions = new ArrayList<>();
        client.pods().list().getItems().forEach(pod -> descriptions.addAll(createDescriptions(pod)));

        return descriptions;
    }

    public Config getConfiguration() {
        return getClient("").getConfiguration();
    }

    private List<ServiceDescription> createDescriptions(Pod pod) {

        List<ServiceDescription> descriptions = new ArrayList<>();

        String group = pod.getMetadata().getNamespace() + "-" + pod.getMetadata().getName();
        pod.getSpec().getContainers().forEach(container -> {
            ServiceDescription description = new ServiceDescription();
            description.setGroup(group);
            description.setName(container.getName());
            description.setIdentifier(container.getName());
            description.setSoftware(container.getImage());
            description.setMachine(pod.getSpec().getNodeName()); //ip?

            // TODO
            //description.setScale(...);
            // statuses: pod.getStatus()
            //description.setNetworks();

            descriptions.add(description);
        });

        return descriptions;
    }

    private KubernetesClient getClient(String context) {
        if (this.client != null)
            return this.client;

        Config config = Config.autoConfigure(context);    //https://github.com/fabric8io/kubernetes-client#configuring-the-client
        KubernetesClient client = new DefaultKubernetesClient(config);
        this.client = client;
        return this.client;
    }
}
