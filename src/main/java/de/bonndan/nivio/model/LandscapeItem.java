package de.bonndan.nivio.model;

import java.util.Map;
import java.util.Set;

public interface LandscapeItem {

    String LAYER_INFRASTRUCTURE = "infrastructure";
    String LAYER_APPLICATION = "applications";
    String LAYER_INGRESS = "ingress";

    String TYPE_SERVICE = "service";

    String IDENTIFIER_VALIDATION = "^[a-z0-9\\.\\:_-]{3,256}$";


    /**
     * Returns the landscape-wide unique identifier of a server or application.
     *
     */
    String getIdentifier();

    /**
     * @return the fqi to identify the landscape item
     */
    FullyQualifiedIdentifier getFullyQualifiedIdentifier();

    /**
     * @return the type (ingress, service, infrastructure)
     */
    String getLayer();

    /**
     * @return the type (service, database, queue, loadbalancer...)
     */
    String getType();

    String getName();
    String getShort_name();
    String getIcon();

    String getGroup();

    String getSoftware();

    String getVersion();

    String getHomepage();

    String getRepository();

    String getContact();

    String getTeam();

    String getVisibility();

    Lifecycle getLifecycle();

    void setStatus(StatusItem statusItem);

    Set<StatusItem> getStatuses();

    String getMachine();

    String getScale();

    String getHost_type();

    Set<String> getNetworks();

    String getDescription();

    String[] getTags();

    Map<String, String> getLabels();

    String getNote();

    String getOwner();

    Set<InterfaceItem> getInterfaces();

    Set<DataFlowItem> getDataFlow();

    /**
     * Describes the capability the service provides for the business, or in case of infrastructure the technical
     * capability like enabling service discovery, configuration, secrets or persistence.
     *
     * @return name
     */
    String getCapability();

    /**
     * Running costs of the service.
     *
     * @return the raw string
     */
    String getCosts();
}
