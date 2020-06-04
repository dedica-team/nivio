package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.ItemDescriptionFactory;
import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.util.URLHelper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ItemDescriptionFactoryKubernetes implements ItemDescriptionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDescriptionFactoryKubernetes.class);

    public static final String NAMESPACE = "namespace";
    public static final String GROUP_LABEL = "groupLabel";

    /**
     * label name to determine the group name (fallback from GROUP_LABEL)
     */
    public static final String APP_KUBERNETES_IO_INSTANCE_LABEL = "app.kubernetes.io/instance";
    public static final String APP_SELECTOR = "app";

    private String namespace = null;
    private String groupLabel = null;
    private KubernetesClient client;

    public ItemDescriptionFactoryKubernetes() {

    }

    public ItemDescriptionFactoryKubernetes(KubernetesClient client) {
        this.client = client;
    }

    @Override
    public List<String> getFormats() {
        return Arrays.asList("kubernetes", "k8s");
    }

    /**
     * Created Items: service -> pod -> containers
     */
    @Override
    public List<ItemDescription> getDescriptions(SourceReference reference, URL baseUrl) {

        try {
            if (!StringUtils.isEmpty(reference.getUrl())) {
                URL url = new URL(reference.getUrl());
                Map<String, String> params = URLHelper.splitQuery(url);
                if (params.containsKey(NAMESPACE)) {
                    namespace = params.get(NAMESPACE);
                }
                if (params.containsKey(GROUP_LABEL)) {
                    this.groupLabel = params.get(GROUP_LABEL);
                }
            }
        } catch (MalformedURLException ignored) {

        }

        KubernetesClient client = getClient(reference.getUrl());

        List<ItemDescription> descriptions = new ArrayList<>();
        final List<ItemDescription> pods = new ArrayList<>();
        getPods().forEach(pod -> {
            ItemDescription podItem = createPodItemDescription(pod);
            descriptions.add(podItem);
            pods.add(podItem);
            List<ItemDescription> descriptionsFromPod = createDescriptionsFromPod(pod, podItem);
            descriptions.addAll(descriptionsFromPod);
        });

        List<Service> services = client.services().list().getItems();
        LOGGER.info("Found services: {}", services.stream().map(service -> service.getMetadata().getName()).collect(Collectors.toList()));
        services.stream()
                .filter(service -> namespace == null || namespace.equals(service.getMetadata().getNamespace()))
                .forEach(service -> descriptions.add(createDescriptionFromService(service, pods)));

        return descriptions;
    }

    /**
     * Creates a pod item
     *
     * @param pod k8s pod object
     * @return pod (yet ungrouped)
     */
    private ItemDescription createPodItemDescription(Pod pod) {
        ItemDescription itemDescription = new ItemDescription();
        itemDescription.setName(pod.getMetadata().getName());
        itemDescription.setIdentifier(pod.getMetadata().getName());
        itemDescription.setType(ItemType.POD);
        pod.getMetadata().getLabels().forEach(itemDescription::setLabel);
        return itemDescription;
    }

    /**
     * @return all pods in the namespace
     */
    private List<Pod> getPods() {
        try {
            List<Pod> pods = client.pods().list().getItems();
            LOGGER.info("Found pods: {}", pods.stream().map(pod -> pod.getMetadata().getName()).collect(Collectors.toList()));
            return pods.stream()
                    .filter(pod -> namespace == null || namespace.equals(pod.getMetadata().getNamespace()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new ProcessingException("Failed to load pods ", ex);
        }
    }

    private ItemDescription createDescriptionFromService(Service kubernetesService, List<ItemDescription> pods) {

        ItemDescription service = new ItemDescription();
        service.setIdentifier(kubernetesService.getMetadata().getName());
        service.setLabel(Label.LAYER, LandscapeItem.LAYER_INGRESS);
        service.setType(kubernetesService.getSpec().getType());

        String group = getGroup(kubernetesService);
        service.setGroup(group);

        String targetId = "";
        Map<String, String> selector = kubernetesService.getSpec().getSelector();
        if (selector != null) {
            targetId = selector.getOrDefault(APP_SELECTOR, null);
        }

        //TODO, check if this is reliable
        if (!StringUtils.isEmpty(targetId)) {
            service.addRelation(new RelationDescription(service.getIdentifier(), targetId));
        }

        //link pods as providers
        pods.stream()
                .filter(pod -> pod.getName().startsWith(service.getIdentifier()))
                .forEach(pod -> {
                    RelationDescription rel = new RelationDescription(pod.getIdentifier(), service.getIdentifier());
                    rel.setType(RelationType.PROVIDER);
                    service.addRelation(rel);
                    pod.setGroup(service.getGroup());
                });

        return service;
    }

    public Config getConfiguration() {
        return getClient("").getConfiguration();
    }

    private List<ItemDescription> createDescriptionsFromPod(Pod pod, ItemDescription podItem) {

        List<ItemDescription> descriptions = new ArrayList<>();

        ItemDescription node = new ItemDescription();
        node.setName(pod.getSpec().getNodeName());
        node.setIdentifier(pod.getSpec().getNodeName());
        node.setType(ItemType.SERVER);
        descriptions.add(node);
        podItem.addRelation(new RelationDescription(node.getIdentifier(), podItem.getIdentifier()));

        String group = getGroup(pod);
        pod.getSpec().getContainers().forEach(container -> {
            ItemDescription containerDesc = new ItemDescription();
            containerDesc.setGroup(group);
            containerDesc.setName(container.getName());
            containerDesc.setIdentifier(podItem.getName() + "-" + container.getName());
            containerDesc.setLabel(Label.SOFTWARE, container.getImage());
            containerDesc.setLabel(Label.MACHINE, pod.getSpec().getNodeName()); //ip?
            containerDesc.setType(ItemType.CONTAINER);
            pod.getMetadata().getLabels().forEach((s, s2) -> containerDesc.setLabel(s, s2));

            //container provides the pod
            RelationDescription relationDescription = new RelationDescription(containerDesc.getIdentifier(), podItem.getIdentifier());
            relationDescription.setType(RelationType.PROVIDER);
            containerDesc.addRelation(relationDescription);

            // TODO
            //description.setScale(...);
            // statuses: pod.getStatus()
            setConditionsAndHealth(pod.getStatus(), podItem);
            podItem.setLabel("hostIP", pod.getStatus().getHostIP());
            podItem.setLabel("podIP", pod.getStatus().getPodIP());
            podItem.setLabel("phase", pod.getStatus().getPhase());
            podItem.setLabel("startTime", pod.getStatus().getStartTime());
            //description.setNetworks();

            descriptions.add(containerDesc);
        });

        pod.getSpec().getVolumes().forEach(volume -> {

            //storing configmap volumes in labels
            if (volume.getConfigMap() != null) {
                podItem.setLabel("configMap" + Label.DELIMITER + volume.getConfigMap().getName(), volume.getConfigMap().getName());
                return;
            }

            ItemDescription volumeDesc = createVolumeDescription(group, volume, pod, podItem);
            descriptions.add(volumeDesc);
        });

        return descriptions;
    }

    private ItemDescription createVolumeDescription(String group, Volume volume, Pod pod, ItemDescription podItem) {
        ItemDescription volumeDesc = new ItemDescription();
        volumeDesc.setGroup(group);
        volumeDesc.setName(volume.getName());
        volumeDesc.setIdentifier(podItem.getName() + "-" + volume.getName());
        volumeDesc.setLabel(Label.MACHINE, pod.getSpec().getNodeName()); //ip?
        volumeDesc.setType(ItemType.VOLUME);
        if (volume.getSecret() != null && volume.getSecret().getSecretName().equals(volume.getName())) {
            volumeDesc.setLabel("secret", 1);
            volumeDesc.setType(ItemType.SECRET);
        }
        pod.getMetadata().getLabels().forEach(volumeDesc::setLabel);

        //volume provides the pod
        RelationDescription relationDescription = new RelationDescription(volumeDesc.getIdentifier(), podItem.getIdentifier());
        relationDescription.setType(RelationType.PROVIDER);
        volumeDesc.addRelation(relationDescription);

        return volumeDesc;
    }

    private void setConditionsAndHealth(PodStatus status, ItemDescription podItem) {
        status.getConditions().forEach(podCondition -> {
            String label = Label.PREFIX_CONDITION + Label.DELIMITER + podCondition.getType();
            podItem.setLabel(label, podCondition.getStatus());
        });
    }

    private String getGroup(HasMetadata hasMetadata) {
        if (groupLabel != null) {
            String labelValue = hasMetadata.getMetadata().getLabels().getOrDefault(groupLabel, "");
            if (!StringUtils.isEmpty(labelValue)) {
                return labelValue;
            }
        }

        String labelValue = hasMetadata.getMetadata().getLabels().getOrDefault(APP_KUBERNETES_IO_INSTANCE_LABEL, "");
        if (!StringUtils.isEmpty(labelValue)) {
            return labelValue;
        }

        return "";
    }

    private KubernetesClient getClient(String context) {
        if (this.client != null)
            return this.client;

        // see https://github.com/fabric8io/kubernetes-client#configuring-the-client
        Config config = Config.autoConfigure(context);

        this.client = new DefaultKubernetesClient(config);
        return this.client;
    }
}
