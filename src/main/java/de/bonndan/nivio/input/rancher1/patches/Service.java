package de.bonndan.nivio.input.rancher1.patches;

import io.rancher.base.AbstractType;
import io.rancher.type.*;

import java.util.List;
import java.util.Map;

/**
 * Copy of {@link io.rancher.type.Service} to fix https://github.com/dedica-team/nivio/issues/323
 */
public class Service extends AbstractType {

    private String accountId;
    private Boolean assignServiceIpAddress;
    private Integer createIndex;
    private String created;
    private Integer currentScale;
    private Map<String, Object> data;
    private String description;
    private String externalId;
    private String fqdn;
    private String healthState;
    private List<String> instanceIds;
    private String kind;
    private LaunchConfig launchConfig;
    private LbTargetConfig lbConfig;
    private Map<String, Object> linkedServices;
    private Map<String, Object> metadata;
    private String name;
    private List<PublicEndpoint> publicEndpoints;
    private String removeTime;
    private String removed;
    private Boolean retainIp;
    private Integer scale;
    private ScalePolicy scalePolicy;
    private List<SecondaryLaunchConfig> secondaryLaunchConfigs;
    private String selectorContainer;
    private String selectorLink;
    private String stackId;
    private Boolean startOnCreate;
    private String state;
    private Boolean system;
    private String transitioning;
    private String transitioningMessage;
    private Integer transitioningProgress;
    private ServiceUpgrade upgrade;
    private String uuid;
    private String vip;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Boolean getAssignServiceIpAddress() {
        return assignServiceIpAddress;
    }

    public void setAssignServiceIpAddress(Boolean assignServiceIpAddress) {
        this.assignServiceIpAddress = assignServiceIpAddress;
    }

    public Integer getCreateIndex() {
        return createIndex;
    }

    public void setCreateIndex(Integer createIndex) {
        this.createIndex = createIndex;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getCurrentScale() {
        return currentScale;
    }

    public void setCurrentScale(Integer currentScale) {
        this.currentScale = currentScale;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    public String getHealthState() {
        return healthState;
    }

    public void setHealthState(String healthState) {
        this.healthState = healthState;
    }

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public LaunchConfig getLaunchConfig() {
        return launchConfig;
    }

    public void setLaunchConfig(LaunchConfig launchConfig) {
        this.launchConfig = launchConfig;
    }

    public LbTargetConfig getLbConfig() {
        return lbConfig;
    }

    public void setLbConfig(LbTargetConfig lbConfig) {
        this.lbConfig = lbConfig;
    }

    public Map<String, Object> getLinkedServices() {
        return linkedServices;
    }

    public void setLinkedServices(Map<String, Object> linkedServices) {
        this.linkedServices = linkedServices;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PublicEndpoint> getPublicEndpoints() {
        return publicEndpoints;
    }

    public void setPublicEndpoints(List<PublicEndpoint> publicEndpoints) {
        this.publicEndpoints = publicEndpoints;
    }

    public String getRemoveTime() {
        return removeTime;
    }

    public void setRemoveTime(String removeTime) {
        this.removeTime = removeTime;
    }

    public String getRemoved() {
        return removed;
    }

    public void setRemoved(String removed) {
        this.removed = removed;
    }

    public Boolean getRetainIp() {
        return retainIp;
    }

    public void setRetainIp(Boolean retainIp) {
        this.retainIp = retainIp;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public ScalePolicy getScalePolicy() {
        return scalePolicy;
    }

    public void setScalePolicy(ScalePolicy scalePolicy) {
        this.scalePolicy = scalePolicy;
    }

    public List<SecondaryLaunchConfig> getSecondaryLaunchConfigs() {
        return secondaryLaunchConfigs;
    }

    public void setSecondaryLaunchConfigs(List<SecondaryLaunchConfig> secondaryLaunchConfigs) {
        this.secondaryLaunchConfigs = secondaryLaunchConfigs;
    }

    public String getSelectorContainer() {
        return selectorContainer;
    }

    public void setSelectorContainer(String selectorContainer) {
        this.selectorContainer = selectorContainer;
    }

    public String getSelectorLink() {
        return selectorLink;
    }

    public void setSelectorLink(String selectorLink) {
        this.selectorLink = selectorLink;
    }

    public String getStackId() {
        return stackId;
    }

    public void setStackId(String stackId) {
        this.stackId = stackId;
    }

    public Boolean getStartOnCreate() {
        return startOnCreate;
    }

    public void setStartOnCreate(Boolean startOnCreate) {
        this.startOnCreate = startOnCreate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public String getTransitioning() {
        return transitioning;
    }

    public void setTransitioning(String transitioning) {
        this.transitioning = transitioning;
    }

    public String getTransitioningMessage() {
        return transitioningMessage;
    }

    public void setTransitioningMessage(String transitioningMessage) {
        this.transitioningMessage = transitioningMessage;
    }

    public Integer getTransitioningProgress() {
        return transitioningProgress;
    }

    public void setTransitioningProgress(Integer transitioningProgress) {
        this.transitioningProgress = transitioningProgress;
    }

    public ServiceUpgrade getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(ServiceUpgrade upgrade) {
        this.upgrade = upgrade;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }
}
