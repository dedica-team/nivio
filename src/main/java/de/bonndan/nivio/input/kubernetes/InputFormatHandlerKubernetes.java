package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.observation.InputFormatObserver;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.bonndan.nivio.input.kubernetes.DeploymentItem.getDeploymentItems;
import static de.bonndan.nivio.input.kubernetes.PersistentVolumeClaimItem.getPersistentVolumeClaimItems;
import static de.bonndan.nivio.input.kubernetes.PersistentVolumeItem.getPersistentVolumeItems;
import static de.bonndan.nivio.input.kubernetes.PodItem.getPodItems;
import static de.bonndan.nivio.input.kubernetes.ReplicaSetItem.getReplicaSetItems;
import static de.bonndan.nivio.input.kubernetes.ServiceItem.getServiceItems;
import static de.bonndan.nivio.input.kubernetes.StatefulSetItem.getStatefulSetItems;

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

        var persistentVolumeClaims = getPersistentVolumeClaimItems(client);
        var persistentVolumes = getPersistentVolumeItems(client);
        crossReferenceClaimer(persistentVolumeClaims, persistentVolumes);

        var itemList = getK8sComponents(client);
        crossReferenceOwner(itemList);

        itemList.addAll(persistentVolumeClaims);
        itemList.addAll(persistentVolumes);

        landscapeDescription.mergeItems(createItemDescription(itemList));
    }

    private ArrayList<Item> getK8sComponents(KubernetesClient client) {
        var itemList = new ArrayList<Item>();
        itemList.addAll(getDeploymentItems(client));
        itemList.addAll(getReplicaSetItems(client));
        itemList.addAll(getPodItems(client));
        itemList.addAll(getServiceItems(client));
        itemList.addAll(getStatefulSetItems(client));
        return itemList;
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

    private void crossReferenceClaimer(List<PersistentVolumeClaimItem> persistentVolumeClaims, List<PersistentVolumeItem> persistentVolumes) {
        persistentVolumes.forEach(item -> {
            var claimer = persistentVolumeClaims.stream().filter(claimItem -> ((PersistentVolume) item.getWrappedItem()).getSpec().getClaimRef().getUid().equals(claimItem.getUid())).collect(Collectors.toList());
            item.setOwners(new ArrayList<>(claimer));
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

    public Config getConfiguration() {
        return getClient("").getConfiguration();
    }
}
