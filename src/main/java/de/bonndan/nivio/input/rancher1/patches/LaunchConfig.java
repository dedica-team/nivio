package de.bonndan.nivio.input.rancher1.patches;

import io.rancher.base.AbstractType;
import io.rancher.type.*;

import java.util.List;
import java.util.Map;


public class LaunchConfig extends AbstractType {
    private String accountId;
    private String agentId;
    private String allocationState;
    private Map<String, Object> blkioDeviceOptions;
    private Integer blkioWeight;
    private DockerBuild build;
    private List<String> capAdd;
    private List<String> capDrop;
    private String cgroupParent;
    private List<String> command;
    private Integer count;
    private Integer cpuCount;
    private Integer cpuPercent;
    private Integer cpuPeriod;
    private Integer cpuQuota;
    private String cpuSet;
    private String cpuSetMems;
    private Integer cpuShares;
    private Integer createIndex;
    private String created;
    private Map<String, Object> data;
    private Map<String, Object> dataVolumeMounts;
    private List<String> dataVolumes;
    private List<String> dataVolumesFrom;
    private List<String> dataVolumesFromLaunchConfigs;
    private String deploymentUnitUuid;
    private String description;
    private List<String> devices;
    private Integer diskQuota;
    private List<VirtualMachineDisk> disks;
    private List<String> dns;
    private List<String> dnsOpt;
    private List<String> dnsSearch;
    private String domainName;
    private List<String> entryPoint;
    private Map<String, Object> environment;
    private List<String> expose;
    private String externalId;
    private List<String> extraHosts;
    private String firstRunning;
    private List<String> groupAdd;
    private InstanceHealthCheck healthCheck;
    private List<String> healthCmd;
    private Integer healthInterval;
    private Integer healthRetries;
    private String healthState;
    private Integer healthTimeout;
    private String hostId;
    private String hostname;
    private String imageUuid;
    private Map<String, Object> instanceLinks;
    private String instanceTriggeredStop;
    private Integer ioMaximumBandwidth;
    private Integer ioMaximumIOps;
    private String ip;
    private String ip6;
    private String ipcMode;
    private String isolation;
    private Integer kernelMemory;
    private String kind;
    private Map<String, Object> labels;
    private LogConfig logConfig;
    private Map<String, Object> lxcConf;
    private Long memory;
    private Long memoryMb;
    private Long memoryReservation;
    private Long memorySwap;
    private Integer memorySwappiness;
    private Integer milliCpuReservation;
    private List<MountEntry> mounts;
    private Boolean nativeContainer;
    private List<String> netAlias;
    private String networkContainerId;
    private List<String> networkIds;
    private String networkLaunchConfig;
    private String networkMode;
    private Boolean oomKillDisable;
    private Integer oomScoreAdj;
    private String pidMode;
    private Integer pidsLimit;
    private List<String> ports;
    private String primaryIpAddress;
    private String primaryNetworkId;
    private Boolean privileged;
    private Boolean publishAllPorts;
    private Boolean readOnly;
    private String registryCredentialId;
    private String removeTime;
    private String removed;
    private String requestedHostId;
    private String requestedIpAddress;
    private List<SecretReference> secrets;
    private List<String> securityOpt;
    private String serviceId;
    private List<String> serviceIds;
    private Integer shmSize;
    private String stackId;
    private Integer startCount;
    private Boolean startOnCreate;
    private String state;
    private Boolean stdinOpen;
    private String stopSignal;
    private Map<String, Object> storageOpt;
    private Map<String, Object> sysctls;
    private Boolean system;
    private Map<String, Object> tmpfs;
    private String token;
    private String transitioning;
    private String transitioningMessage;
    private Integer transitioningProgress;
    private Boolean tty;
    private List<Ulimit> ulimits;
    private String user;
    private List<String> userPorts;
    private String userdata;
    private String usernsMode;
    private String uts;
    private String uuid;
    private Integer vcpu;
    private String version;
    private String volumeDriver;
    private String workingDir;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAllocationState() {
        return allocationState;
    }

    public void setAllocationState(String allocationState) {
        this.allocationState = allocationState;
    }

    public Map<String, Object> getBlkioDeviceOptions() {
        return blkioDeviceOptions;
    }

    public void setBlkioDeviceOptions(Map<String, Object> blkioDeviceOptions) {
        this.blkioDeviceOptions = blkioDeviceOptions;
    }

    public Integer getBlkioWeight() {
        return blkioWeight;
    }

    public void setBlkioWeight(Integer blkioWeight) {
        this.blkioWeight = blkioWeight;
    }

    public DockerBuild getBuild() {
        return build;
    }

    public void setBuild(DockerBuild build) {
        this.build = build;
    }

    public List<String> getCapAdd() {
        return capAdd;
    }

    public void setCapAdd(List<String> capAdd) {
        this.capAdd = capAdd;
    }

    public List<String> getCapDrop() {
        return capDrop;
    }

    public void setCapDrop(List<String> capDrop) {
        this.capDrop = capDrop;
    }

    public String getCgroupParent() {
        return cgroupParent;
    }

    public void setCgroupParent(String cgroupParent) {
        this.cgroupParent = cgroupParent;
    }

    public List<String> getCommand() {
        return command;
    }

    public void setCommand(List<String> command) {
        this.command = command;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCpuCount() {
        return cpuCount;
    }

    public void setCpuCount(Integer cpuCount) {
        this.cpuCount = cpuCount;
    }

    public Integer getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(Integer cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public Integer getCpuPeriod() {
        return cpuPeriod;
    }

    public void setCpuPeriod(Integer cpuPeriod) {
        this.cpuPeriod = cpuPeriod;
    }

    public Integer getCpuQuota() {
        return cpuQuota;
    }

    public void setCpuQuota(Integer cpuQuota) {
        this.cpuQuota = cpuQuota;
    }

    public String getCpuSet() {
        return cpuSet;
    }

    public void setCpuSet(String cpuSet) {
        this.cpuSet = cpuSet;
    }

    public String getCpuSetMems() {
        return cpuSetMems;
    }

    public void setCpuSetMems(String cpuSetMems) {
        this.cpuSetMems = cpuSetMems;
    }

    public Integer getCpuShares() {
        return cpuShares;
    }

    public void setCpuShares(Integer cpuShares) {
        this.cpuShares = cpuShares;
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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getDataVolumeMounts() {
        return dataVolumeMounts;
    }

    public void setDataVolumeMounts(Map<String, Object> dataVolumeMounts) {
        this.dataVolumeMounts = dataVolumeMounts;
    }

    public List<String> getDataVolumes() {
        return dataVolumes;
    }

    public void setDataVolumes(List<String> dataVolumes) {
        this.dataVolumes = dataVolumes;
    }

    public List<String> getDataVolumesFrom() {
        return dataVolumesFrom;
    }

    public void setDataVolumesFrom(List<String> dataVolumesFrom) {
        this.dataVolumesFrom = dataVolumesFrom;
    }

    public List<String> getDataVolumesFromLaunchConfigs() {
        return dataVolumesFromLaunchConfigs;
    }

    public void setDataVolumesFromLaunchConfigs(List<String> dataVolumesFromLaunchConfigs) {
        this.dataVolumesFromLaunchConfigs = dataVolumesFromLaunchConfigs;
    }

    public String getDeploymentUnitUuid() {
        return deploymentUnitUuid;
    }

    public void setDeploymentUnitUuid(String deploymentUnitUuid) {
        this.deploymentUnitUuid = deploymentUnitUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public Integer getDiskQuota() {
        return diskQuota;
    }

    public void setDiskQuota(Integer diskQuota) {
        this.diskQuota = diskQuota;
    }

    public List<VirtualMachineDisk> getDisks() {
        return disks;
    }

    public void setDisks(List<VirtualMachineDisk> disks) {
        this.disks = disks;
    }

    public List<String> getDns() {
        return dns;
    }

    public void setDns(List<String> dns) {
        this.dns = dns;
    }

    public List<String> getDnsOpt() {
        return dnsOpt;
    }

    public void setDnsOpt(List<String> dnsOpt) {
        this.dnsOpt = dnsOpt;
    }

    public List<String> getDnsSearch() {
        return dnsSearch;
    }

    public void setDnsSearch(List<String> dnsSearch) {
        this.dnsSearch = dnsSearch;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<String> getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(List<String> entryPoint) {
        this.entryPoint = entryPoint;
    }

    public Map<String, Object> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, Object> environment) {
        this.environment = environment;
    }

    public List<String> getExpose() {
        return expose;
    }

    public void setExpose(List<String> expose) {
        this.expose = expose;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<String> getExtraHosts() {
        return extraHosts;
    }

    public void setExtraHosts(List<String> extraHosts) {
        this.extraHosts = extraHosts;
    }

    public String getFirstRunning() {
        return firstRunning;
    }

    public void setFirstRunning(String firstRunning) {
        this.firstRunning = firstRunning;
    }

    public List<String> getGroupAdd() {
        return groupAdd;
    }

    public void setGroupAdd(List<String> groupAdd) {
        this.groupAdd = groupAdd;
    }

    public InstanceHealthCheck getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(InstanceHealthCheck healthCheck) {
        this.healthCheck = healthCheck;
    }

    public List<String> getHealthCmd() {
        return healthCmd;
    }

    public void setHealthCmd(List<String> healthCmd) {
        this.healthCmd = healthCmd;
    }

    public Integer getHealthInterval() {
        return healthInterval;
    }

    public void setHealthInterval(Integer healthInterval) {
        this.healthInterval = healthInterval;
    }

    public Integer getHealthRetries() {
        return healthRetries;
    }

    public void setHealthRetries(Integer healthRetries) {
        this.healthRetries = healthRetries;
    }

    public String getHealthState() {
        return healthState;
    }

    public void setHealthState(String healthState) {
        this.healthState = healthState;
    }

    public Integer getHealthTimeout() {
        return healthTimeout;
    }

    public void setHealthTimeout(Integer healthTimeout) {
        this.healthTimeout = healthTimeout;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }

    public Map<String, Object> getInstanceLinks() {
        return instanceLinks;
    }

    public void setInstanceLinks(Map<String, Object> instanceLinks) {
        this.instanceLinks = instanceLinks;
    }

    public String getInstanceTriggeredStop() {
        return instanceTriggeredStop;
    }

    public void setInstanceTriggeredStop(String instanceTriggeredStop) {
        this.instanceTriggeredStop = instanceTriggeredStop;
    }

    public Integer getIoMaximumBandwidth() {
        return ioMaximumBandwidth;
    }

    public void setIoMaximumBandwidth(Integer ioMaximumBandwidth) {
        this.ioMaximumBandwidth = ioMaximumBandwidth;
    }

    public Integer getIoMaximumIOps() {
        return ioMaximumIOps;
    }

    public void setIoMaximumIOps(Integer ioMaximumIOps) {
        this.ioMaximumIOps = ioMaximumIOps;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp6() {
        return ip6;
    }

    public void setIp6(String ip6) {
        this.ip6 = ip6;
    }

    public String getIpcMode() {
        return ipcMode;
    }

    public void setIpcMode(String ipcMode) {
        this.ipcMode = ipcMode;
    }

    public String getIsolation() {
        return isolation;
    }

    public void setIsolation(String isolation) {
        this.isolation = isolation;
    }

    public Integer getKernelMemory() {
        return kernelMemory;
    }

    public void setKernelMemory(Integer kernelMemory) {
        this.kernelMemory = kernelMemory;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Map<String, Object> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }

    public LogConfig getLogConfig() {
        return logConfig;
    }

    public void setLogConfig(LogConfig logConfig) {
        this.logConfig = logConfig;
    }

    public Map<String, Object> getLxcConf() {
        return lxcConf;
    }

    public void setLxcConf(Map<String, Object> lxcConf) {
        this.lxcConf = lxcConf;
    }

    public Long getMemory() {
        return memory;
    }

    public void setMemory(Long memory) {
        this.memory = memory;
    }

    public Long getMemoryMb() {
        return memoryMb;
    }

    public void setMemoryMb(Long memoryMb) {
        this.memoryMb = memoryMb;
    }

    public Long getMemoryReservation() {
        return memoryReservation;
    }

    public void setMemoryReservation(Long memoryReservation) {
        this.memoryReservation = memoryReservation;
    }

    public Long getMemorySwap() {
        return memorySwap;
    }

    public void setMemorySwap(Long memorySwap) {
        this.memorySwap = memorySwap;
    }

    public Integer getMemorySwappiness() {
        return memorySwappiness;
    }

    public void setMemorySwappiness(Integer memorySwappiness) {
        this.memorySwappiness = memorySwappiness;
    }

    public Integer getMilliCpuReservation() {
        return milliCpuReservation;
    }

    public void setMilliCpuReservation(Integer milliCpuReservation) {
        this.milliCpuReservation = milliCpuReservation;
    }

    public List<MountEntry> getMounts() {
        return mounts;
    }

    public void setMounts(List<MountEntry> mounts) {
        this.mounts = mounts;
    }

    public Boolean getNativeContainer() {
        return nativeContainer;
    }

    public void setNativeContainer(Boolean nativeContainer) {
        this.nativeContainer = nativeContainer;
    }

    public List<String> getNetAlias() {
        return netAlias;
    }

    public void setNetAlias(List<String> netAlias) {
        this.netAlias = netAlias;
    }

    public String getNetworkContainerId() {
        return networkContainerId;
    }

    public void setNetworkContainerId(String networkContainerId) {
        this.networkContainerId = networkContainerId;
    }

    public List<String> getNetworkIds() {
        return networkIds;
    }

    public void setNetworkIds(List<String> networkIds) {
        this.networkIds = networkIds;
    }

    public String getNetworkLaunchConfig() {
        return networkLaunchConfig;
    }

    public void setNetworkLaunchConfig(String networkLaunchConfig) {
        this.networkLaunchConfig = networkLaunchConfig;
    }

    public String getNetworkMode() {
        return networkMode;
    }

    public void setNetworkMode(String networkMode) {
        this.networkMode = networkMode;
    }

    public Boolean getOomKillDisable() {
        return oomKillDisable;
    }

    public void setOomKillDisable(Boolean oomKillDisable) {
        this.oomKillDisable = oomKillDisable;
    }

    public Integer getOomScoreAdj() {
        return oomScoreAdj;
    }

    public void setOomScoreAdj(Integer oomScoreAdj) {
        this.oomScoreAdj = oomScoreAdj;
    }

    public String getPidMode() {
        return pidMode;
    }

    public void setPidMode(String pidMode) {
        this.pidMode = pidMode;
    }

    public Integer getPidsLimit() {
        return pidsLimit;
    }

    public void setPidsLimit(Integer pidsLimit) {
        this.pidsLimit = pidsLimit;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }

    public String getPrimaryIpAddress() {
        return primaryIpAddress;
    }

    public void setPrimaryIpAddress(String primaryIpAddress) {
        this.primaryIpAddress = primaryIpAddress;
    }

    public String getPrimaryNetworkId() {
        return primaryNetworkId;
    }

    public void setPrimaryNetworkId(String primaryNetworkId) {
        this.primaryNetworkId = primaryNetworkId;
    }

    public Boolean getPrivileged() {
        return privileged;
    }

    public void setPrivileged(Boolean privileged) {
        this.privileged = privileged;
    }

    public Boolean getPublishAllPorts() {
        return publishAllPorts;
    }

    public void setPublishAllPorts(Boolean publishAllPorts) {
        this.publishAllPorts = publishAllPorts;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getRegistryCredentialId() {
        return registryCredentialId;
    }

    public void setRegistryCredentialId(String registryCredentialId) {
        this.registryCredentialId = registryCredentialId;
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

    public String getRequestedHostId() {
        return requestedHostId;
    }

    public void setRequestedHostId(String requestedHostId) {
        this.requestedHostId = requestedHostId;
    }

    public String getRequestedIpAddress() {
        return requestedIpAddress;
    }

    public void setRequestedIpAddress(String requestedIpAddress) {
        this.requestedIpAddress = requestedIpAddress;
    }

    public List<SecretReference> getSecrets() {
        return secrets;
    }

    public void setSecrets(List<SecretReference> secrets) {
        this.secrets = secrets;
    }

    public List<String> getSecurityOpt() {
        return securityOpt;
    }

    public void setSecurityOpt(List<String> securityOpt) {
        this.securityOpt = securityOpt;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public List<String> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<String> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public Integer getShmSize() {
        return shmSize;
    }

    public void setShmSize(Integer shmSize) {
        this.shmSize = shmSize;
    }

    public String getStackId() {
        return stackId;
    }

    public void setStackId(String stackId) {
        this.stackId = stackId;
    }

    public Integer getStartCount() {
        return startCount;
    }

    public void setStartCount(Integer startCount) {
        this.startCount = startCount;
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

    public Boolean getStdinOpen() {
        return stdinOpen;
    }

    public void setStdinOpen(Boolean stdinOpen) {
        this.stdinOpen = stdinOpen;
    }

    public String getStopSignal() {
        return stopSignal;
    }

    public void setStopSignal(String stopSignal) {
        this.stopSignal = stopSignal;
    }

    public Map<String, Object> getStorageOpt() {
        return storageOpt;
    }

    public void setStorageOpt(Map<String, Object> storageOpt) {
        this.storageOpt = storageOpt;
    }

    public Map<String, Object> getSysctls() {
        return sysctls;
    }

    public void setSysctls(Map<String, Object> sysctls) {
        this.sysctls = sysctls;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public Map<String, Object> getTmpfs() {
        return tmpfs;
    }

    public void setTmpfs(Map<String, Object> tmpfs) {
        this.tmpfs = tmpfs;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public Boolean getTty() {
        return tty;
    }

    public void setTty(Boolean tty) {
        this.tty = tty;
    }

    public List<Ulimit> getUlimits() {
        return ulimits;
    }

    public void setUlimits(List<Ulimit> ulimits) {
        this.ulimits = ulimits;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<String> getUserPorts() {
        return userPorts;
    }

    public void setUserPorts(List<String> userPorts) {
        this.userPorts = userPorts;
    }

    public String getUserdata() {
        return userdata;
    }

    public void setUserdata(String userdata) {
        this.userdata = userdata;
    }

    public String getUsernsMode() {
        return usernsMode;
    }

    public void setUsernsMode(String usernsMode) {
        this.usernsMode = usernsMode;
    }

    public String getUts() {
        return uts;
    }

    public void setUts(String uts) {
        this.uts = uts;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getVcpu() {
        return vcpu;
    }

    public void setVcpu(Integer vcpu) {
        this.vcpu = vcpu;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVolumeDriver() {
        return volumeDriver;
    }

    public void setVolumeDriver(String volumeDriver) {
        this.volumeDriver = volumeDriver;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }
}