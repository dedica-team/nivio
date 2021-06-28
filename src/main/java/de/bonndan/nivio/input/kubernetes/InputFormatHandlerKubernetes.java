package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.observation.InputFormatObserver;
import de.bonndan.nivio.util.URLHelper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Scans the k8s api for services, pods, volumes etc.
 */
@org.springframework.stereotype.Service
public class InputFormatHandlerKubernetes implements InputFormatHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputFormatHandlerKubernetes.class);

    public static final String NAMESPACE = "namespace";
    public static final String GROUP_LABEL_PARAM = "groupLabel";

    /**
     * label name to determine the group name (fallback from GROUP_LABEL)
     */
    public static final String APP_KUBERNETES_IO_INSTANCE_LABEL = "app.kubernetes.io/instance";
    public static final String APP_SELECTOR = "app";

    private String namespace = null;
    private String groupLabel = null;
    private KubernetesClient client;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public InputFormatHandlerKubernetes(Optional<KubernetesClient> client) {
        this.client = client.orElse(null);
    }

    @Override
    public List<String> getFormats() {
        return Arrays.asList("kubernetes", "k8s");
    }

    /**
     * Created Items: service -> pod -> containers
     */
    @Override
    public void applyData(SourceReference reference, URL baseUrl, LandscapeDescription landscapeDescription) {

        try {
            if (!StringUtils.isEmpty(reference.getUrl())) {
                URL url = new URL(reference.getUrl());
                Map<String, String> params = URLHelper.splitQuery(url);
                if (params.containsKey(NAMESPACE)) {
                    namespace = params.get(NAMESPACE);
                }
                if (params.containsKey(GROUP_LABEL_PARAM)) {
                    this.groupLabel = params.get(GROUP_LABEL_PARAM);
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
        List<ItemDescription> itemServices = new ArrayList<>();
        LOGGER.info("Found services: {}", services.stream().map(service -> service.getMetadata().getName()).collect(Collectors.toList()));
        services.stream()
                .filter(service -> namespace == null || namespace.equals(service.getMetadata().getNamespace()))
                .forEach(service -> itemServices.add(createDescriptionFromService(service, pods)));

        descriptions.addAll(itemServices);
        //landscapeDescription.mergeItems(descriptions);

        var deployments = getDeployments(client);
        var replicaSets = getReplicaSets(client);
        var podList = getPodItems();
        var serviceList = getServiceItems(client);
        var statefulSets = getStatefulSet(client);

        var itemList = new ArrayList<Item>();
        itemList.addAll(deployments);
        itemList.addAll(replicaSets);
        itemList.addAll(podList);
        itemList.addAll(serviceList);
        itemList.addAll(statefulSets);
        crossReferenceOwner(itemList);
        var e = createItemDescription(itemList);
        landscapeDescription.mergeItems(e);
    }

    private List<ItemDescription> createItemDescription(List<Item> itemList) {
        return itemList.stream().map(item -> {
            var itemDescription = new ItemDescription();
            itemDescription.setIdentifier(item.getUid());
            itemDescription.setName(item.getName());
            itemDescription.setType(item.getType());
            if (!item.getOwner().isEmpty()) {
                itemDescription.setOwner(item.getOwner().get(0).getName());
            }
            itemDescription.setGroup(item.getGroup());
            item.getOwner().forEach(owner -> itemDescription.addRelation(new RelationDescription(owner.getUid(), item.getUid())));
            if (!item.getStatus().isEmpty()) {
                item.getStatus().forEach((k, v) -> itemDescription.setLabel(Label.condition.withPrefix(k), v));
            }
            return itemDescription;
        }).collect(Collectors.toList());
    }

    private void crossReferenceOwner(ArrayList<Item> items) {
        items.forEach(item -> {
            var owners = new ArrayList<Item>();
            owners = (ArrayList<Item>) items.stream().filter(item1 -> item.getWrappedItem().getMetadata().getOwnerReferences().stream().map(OwnerReference::getUid).collect(Collectors.toList()).contains(item1.getUid())).collect(Collectors.toList());
            item.setOwners(owners);
        });
    }

    private List<PodItem> getPodItems() {
        var pods = getPods();
        return pods.stream().map(pod -> {
            var podItem = new PodItem();
            podItem.setType(ItemType.POD);
            podItem.setPod(pod);
            podItem.setName(pod.getMetadata().getName());
            podItem.setUid(pod.getMetadata().getUid());
            return podItem;
        }).collect(Collectors.toList());
    }

    private List<DeploymentItem> getDeployments(KubernetesClient client) {
        var deploymentList = client.apps().deployments().list().getItems();
        return deploymentList.stream().map(deployment -> {
            var deploymentItem = new DeploymentItem();
            deploymentItem.setType(ItemType.DEPLOYMENT);
            deploymentItem.setDeployment(deployment);
            deploymentItem.setName(deployment.getMetadata().getName());
            deploymentItem.setUid(deployment.getMetadata().getUid());
            deployment.getStatus().getConditions().forEach(condition -> deploymentItem.addStatus(condition.getType(), condition.getStatus()));
            return deploymentItem;
        }).collect(Collectors.toList());
    }

    private List<ReplicaSetItem> getReplicaSets(KubernetesClient client) {
        var replicaSetList = client.apps().replicaSets().list().getItems();
        return replicaSetList.stream().map(replicaSet -> {
            var replicaSetItem = new ReplicaSetItem();
            replicaSetItem.setType(ItemType.REPLICASET);
            replicaSetItem.setReplicaSet(replicaSet);
            replicaSetItem.setName(replicaSet.getMetadata().getName());
            replicaSetItem.setUid(replicaSet.getMetadata().getUid());
            return replicaSetItem;
        }).collect(Collectors.toList());
    }

    private List<ServiceItem> getServiceItems(KubernetesClient client) {
        var serviceList = client.services().list().getItems();
        return serviceList.stream().map(service -> {
            var serviceItem = new ServiceItem();
            serviceItem.setType(ItemType.SERVICE);
            serviceItem.setService(service);
            serviceItem.setName(service.getMetadata().getName());
            serviceItem.setUid(service.getMetadata().getUid());
            return serviceItem;
        }).collect(Collectors.toList());
    }

    private List<StatefulSetItem> getStatefulSet(KubernetesClient client) {
        var statefulSetList = client.apps().statefulSets().list().getItems();
        return statefulSetList.stream().map(statefulSet -> {
            var statefulSetItem = new StatefulSetItem();
            statefulSetItem.setType(ItemType.STATEFULSET);
            statefulSetItem.setStatefulSet(statefulSet);
            statefulSetItem.setName(statefulSet.getMetadata().getName());
            statefulSetItem.setUid(statefulSet.getMetadata().getUid());
            return statefulSetItem;
        }).collect(Collectors.toList());
    }

    @Override
    public InputFormatObserver getObserver(@NonNull final InputFormatObserver inner, @NonNull final SourceReference sourceReference) {
        return null;
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
        itemDescription.setGroup(getGroup(pod));
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
        service.setType(ItemType.SERVICE);

        String group = getGroup(kubernetesService);
        service.setGroup(group);

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
            containerDesc.setLabel(Label.software, container.getImage());
            containerDesc.setType(ItemType.CONTAINER);
            pod.getMetadata().getLabels().forEach(containerDesc::setLabel);

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
                podItem.setLabel(Label.withPrefix("configMap", volume.getConfigMap().getName()), volume.getConfigMap().getName());
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
        if (status != null && status.getConditions() != null) {
            status.getConditions().forEach(podCondition -> {
                podItem.setLabel(Label.condition.withPrefix(podCondition.getType()), podCondition.getStatus());
            });
        }
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
