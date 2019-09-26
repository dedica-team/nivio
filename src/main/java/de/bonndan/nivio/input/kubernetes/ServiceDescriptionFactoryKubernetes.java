package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ServiceDescriptionFactory;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.landscape.ServiceItems;
import de.bonndan.nivio.util.URLHelper;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ServiceDescriptionFactoryKubernetes implements ServiceDescriptionFactory {

    public static final String NAMESPACE = "namespace";
    public static final String GROUP_LABEL = "groupLabel";

    private String namespace = null;
    private String groupLabel = null;
    private KubernetesClient client;

    public ServiceDescriptionFactoryKubernetes(SourceReference reference) {
        try {
            if (!StringUtils.isEmpty(reference.getUrl())) {
                URL url = new URL(reference.getUrl());
                Map<String, String> params = URLHelper.splitQuery(url);
                if (params.containsKey(NAMESPACE))
                    namespace = params.get(NAMESPACE);
                if (params.containsKey(GROUP_LABEL))
                    this.groupLabel = params.get(GROUP_LABEL);
            }
        } catch (MalformedURLException ignored) {

        }
    }

    public ServiceDescriptionFactoryKubernetes(SourceReference reference, KubernetesClient client) {
        this(reference);
        this.client = client;
    }

    @Override
    public List<ServiceDescription> fromString(String context) {
        KubernetesClient client = getClient(context);

        List<ServiceDescription> descriptions = new ArrayList<>();
        client.pods().list().getItems().stream()
                .filter(pod -> {
                    if (namespace == null)
                        return true;
                    return namespace.equals(pod.getMetadata().getNamespace());
                })
                .forEach(pod -> descriptions.addAll(createDescriptionFromService(pod)));

        client.services().list().getItems().stream()
                .filter(service -> {
                    if (namespace == null)
                        return true;
                    return namespace.equals(service.getMetadata().getNamespace());
                })
                .forEach(service -> descriptions.addAll(createDescriptionFromService(service, descriptions)));

        return descriptions;
    }

    private Collection<? extends ServiceDescription> createDescriptionFromService(Service kubernetesService, List<ServiceDescription> items) {
        List<ServiceDescription> descriptions = new ArrayList<>();

        ServiceDescription description = new ServiceDescription();
        description.setIdentifier(kubernetesService.getMetadata().getName());
        description.setLayer(ServiceItem.LAYER_INGRESS);
        description.setType(kubernetesService.getSpec().getType());

        String group = getGroup(kubernetesService);
        description.setGroup(group);

        String targetId = kubernetesService.getSpec().getSelector().get("app");
        Optional.ofNullable(ServiceItems.find(targetId, group, items)).ifPresent(provider -> {
            ((ServiceDescription) provider.get()).getProvided_by().add(description.getIdentifier());
        });

        descriptions.add(description);

        return descriptions;
    }

    public Config getConfiguration() {
        return getClient("").getConfiguration();
    }

    private List<ServiceDescription> createDescriptionFromService(Pod pod) {

        List<ServiceDescription> descriptions = new ArrayList<>();

        String group = getGroup(pod);
        pod.getSpec().getContainers().forEach(container -> {
            ServiceDescription description = new ServiceDescription();
            description.setGroup(group);
            description.setName(container.getName());
            description.setIdentifier(container.getName());
            description.setSoftware(container.getImage());
            description.setMachine(pod.getSpec().getNodeName()); //ip?

            // TODO
            //set Labels, introduce new labels property (docker/k8s)
            //description.getProvided_by().add(pod.getSpec().)
            //description.setScale(...);
            // statuses: pod.getStatus()
            //description.setNetworks();

            descriptions.add(description);
        });

        return descriptions;
    }

    private String getGroup(HasMetadata hasMetadata) {
        if (groupLabel != null)
            return hasMetadata.getMetadata().getLabels().getOrDefault(groupLabel, "");

        return "";
    }

    private KubernetesClient getClient(String context) {
        if (this.client != null)
            return this.client;

        Config config = Config.autoConfigure(context);    //https://github.com/fabric8io/kubernetes-client#configuring-the-client

        this.client = new DefaultKubernetesClient(config);
        return this.client;
    }
}
