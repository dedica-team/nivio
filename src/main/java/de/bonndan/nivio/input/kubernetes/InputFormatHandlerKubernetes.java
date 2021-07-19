package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.observation.InputFormatObserver;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static de.bonndan.nivio.input.kubernetes.itemadapters.DeploymentItemAdapter.getDeploymentItems;
import static de.bonndan.nivio.input.kubernetes.itemadapters.PersistentVolumeClaimItemAdapter.getPersistentVolumeClaimItems;
import static de.bonndan.nivio.input.kubernetes.itemadapters.PersistentVolumeItemAdapter.getPersistentVolumeItems;
import static de.bonndan.nivio.input.kubernetes.itemadapters.PodItemAdapter.getPodItems;
import static de.bonndan.nivio.input.kubernetes.itemadapters.ReplicaSetItemAdapter.getReplicaSetItems;
import static de.bonndan.nivio.input.kubernetes.itemadapters.ServiceItemAdapter.getServiceItems;
import static de.bonndan.nivio.input.kubernetes.itemadapters.StatefulSetItemAdapter.getStatefulSetItems;

/**
 * Scans the k8s api for services, pods, volumes etc.
 */
@org.springframework.stereotype.Service
public class InputFormatHandlerKubernetes implements InputFormatHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputFormatHandlerKubernetes.class);

    public static final String NAMESPACE = "namespace";

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

        this.client = getClient(reference.getUrl());

        try {
            client.getVersion();
            landscapeDescription.mergeItems(getItemDescription(client));
        } catch (KubernetesClientException n) {
            LOGGER.error(n.getMessage());
            LOGGER.error("Kubernetes might not be available");
        }
    }

    private List<ItemDescription> getItemDescription(KubernetesClient client) {
        var persistentVolumeClaims = getPersistentVolumeClaimItems(client);
        var persistentVolumes = getPersistentVolumeItems(client);
        crossReferenceClaimer(persistentVolumeClaims, persistentVolumes);

        var serviceItems = getServiceItems(client);
        var deploymentItems = getDeploymentItems(client);
        var statefulSetItems = getStatefulSetItems(client);

        crossReferenceService(serviceItems, deploymentItems);
        crossReferenceService(serviceItems, statefulSetItems);

        var podItems = getPodItems(client);
        crossReferenceVolumes(persistentVolumeClaims, podItems);

        var itemList = new ArrayList<K8sItem>();
        itemList.addAll(getReplicaSetItems(client));
        itemList.addAll(podItems);
        itemList.addAll(serviceItems);
        itemList.addAll(deploymentItems);
        itemList.addAll(statefulSetItems);
        crossReferenceOwner(itemList);

        itemList.addAll(persistentVolumeClaims);
        itemList.addAll(persistentVolumes);
        if (K8sJsonParser.getExperimentalActive()) {
            crossReferenceLabel(itemList);
        }
        return createItemDescription(itemList);
    }

    private List<ItemDescription> createItemDescription(List<K8sItem> itemList) {
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
            if (!item.getDetails().isEmpty()) {
                item.getDetails().forEach(itemDescription::setLabel);
            }
            return itemDescription;
        }).collect(Collectors.toList());
    }

    private void crossReferenceLabel(ArrayList<K8sItem> itemList) {
        itemList.forEach(ownedItem -> {
            var ownerList = itemList.stream().filter(
                    ownerItem -> CollectionUtils.intersection(Objects.requireNonNullElse(ownedItem.getItemAdapter().getWrappedItem().getMetadata().getLabels(), new HashMap<String, String>()).values(),
                            Objects.requireNonNullElse(ownerItem.getItemAdapter().getWrappedItem().getMetadata().getLabels(), new HashMap<String, String>()).values())
                            .size() >= 2 && ownerItem.getLevelDecorator().getLevel() != -1 && ownedItem.getLevelDecorator().getLevel() != -1 &&
                            (ownerItem.getLevelDecorator().getLevel() - ownedItem.getLevelDecorator().getLevel()) == 1).collect(Collectors.toList());
            ownerList.forEach(ownedItem::addOwner);
        });
    }

    private void crossReferenceVolumes(List<K8sItem> persistentVolumeClaimList, List<K8sItem> podList) {
        persistentVolumeClaimList.forEach(persistentVolume -> {
            var owners = new ArrayList<K8sItem>();
            owners = (ArrayList<K8sItem>) podList.stream().filter(pod -> ((Pod) pod.getItemAdapter().getWrappedItem()).getSpec().getVolumes().stream().filter(volume -> volume.getPersistentVolumeClaim() != null).map(volumeNonNull -> volumeNonNull.getPersistentVolumeClaim().getClaimName()).collect(Collectors.toList()).contains(persistentVolume.getName()))
                    .collect(Collectors.toList());
            owners.forEach(persistentVolume::addOwner);
        });
    }

    private void crossReferenceOwner(ArrayList<K8sItem> itemList) {
        itemList.forEach(ownedItem -> {
            var owners = new ArrayList<K8sItem>();
            owners = (ArrayList<K8sItem>) itemList.stream().filter(ownerItem -> ownedItem.getItemAdapter().getWrappedItem().getMetadata().getOwnerReferences().stream().map(OwnerReference::getUid).collect(Collectors.toList()).contains(ownerItem.getUid())).collect(Collectors.toList());
            owners.forEach(ownedItem::addOwner);
        });
    }

    private void crossReferenceClaimer(List<K8sItem> persistentVolumeClaims, List<K8sItem> persistentVolumes) {
        persistentVolumes.forEach(ownedItem -> {
            var claimer = persistentVolumeClaims.stream().filter(claimItem -> ((PersistentVolume) ownedItem.getItemAdapter().getWrappedItem()).getSpec().getClaimRef().getUid().equals(claimItem.getUid())).collect(Collectors.toList());
            claimer.forEach(ownedItem::addOwner);
        });
    }

    private void crossReferenceService(List<K8sItem> service, List<K8sItem> owners) {
        service.forEach(ownedItem -> {
            var claimer = owners.stream().filter(claimItem -> (ownedItem.getName().equals(claimItem.getName()))).collect(Collectors.toList());
            claimer.forEach(ownedItem::addOwner);
        });
    }

    @Override
    public InputFormatObserver getObserver(@NonNull final InputFormatObserver inner, @NonNull final SourceReference sourceReference) {
        return null;
    }

    private KubernetesClient getClient(String context) {
        if (this.client != null)
            return this.client;

        // see https://github.com/fabric8io/kubernetes-client#configuring-the-client
        var config = Config.autoConfigure(context);

        this.client = new DefaultKubernetesClient(config);
        return this.client;
    }
}
