package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.ReadingException;
import de.bonndan.nivio.landscape.LandscapeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDescriptionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDescriptionFactory.class);
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    public static List<ServiceDescription> fromYaml(String yml) {

        List<ServiceDescription> services = new ArrayList<>();

        Source source = null;
        try {
            source = mapper.readValue(yml, Source.class);
        } catch (IOException e) {
            logger.error("Failed to read yml", e);
        }
        if (source == null) {
            logger.warn("Got null out of yml string " + yml);
            return services;
        }
        source.ingress.forEach(serviceDescription -> {
            serviceDescription.setType(LandscapeItem.TYPE_INGRESS);
            services.add(serviceDescription);
        });
        source.services.forEach(serviceDescription -> {
            serviceDescription.setType(LandscapeItem.TYPE_APPLICATION);
            services.add(serviceDescription);
        });
        source.infrastructure.forEach(serviceDescription -> {
            serviceDescription.setType(LandscapeItem.TYPE_INFRASTRUCTURE);
            services.add(serviceDescription);
        });

        return services;

    }

    static void assignNotNull(ServiceDescription existing, ServiceDescription increment) {
        if (increment.getName() != null)
            existing.setName(increment.getName());
        if (increment.getDescription() != null)
            existing.setDescription(increment.getDescription());
        if (increment.getShort_name() != null)
            existing.setShort_name(increment.getShort_name());
        if (increment.getHomepage() != null)
            existing.setHomepage(increment.getHomepage());
        if (increment.getRepository() != null)
            existing.setRepository(increment.getRepository());
        if (increment.getContact() != null)
            existing.setContact(increment.getContact());
        if (increment.getStatuses() != null)
            existing.setStatuses(increment.getStatuses());
        if (increment.getOwner() != null)
            existing.setOwner(increment.getOwner());
        if (increment.getTeam() != null)
            existing.setTeam(increment.getTeam());
        if (increment.getGroup() != null)
            existing.setGroup(increment.getGroup());
        if (increment.getNote() != null)
            existing.setNote(increment.getNote());
        if (increment.getTags() != null)
            existing.setTags(increment.getTags());


        if (increment.getDataFlow() != null)
            existing.setDataFlow(increment.getDataFlow());
        if (increment.getInterfaces() != null)
            existing.setInterfaces(increment.getInterfaces());
        if (increment.getProvided_by() != null)
            existing.setProvided_by(increment.getProvided_by());
        if (increment.getNetwork() != null)
            existing.setNetwork(increment.getNetwork());
        if (increment.getMachine() != null)
            existing.setMachine(increment.getMachine());
        if (increment.getPort() != null)
            existing.setPort(increment.getPort());
        if (increment.getProtocol() != null)
            existing.setProtocol(increment.getProtocol());
        if (increment.getSoftware() != null)
            existing.setSoftware(increment.getSoftware());
        if (increment.getStatuses() != null)
            existing.setStatuses(increment.getStatuses());
        if (increment.getScale() != null)
            existing.setScale(increment.getScale());
        if (increment.getHost_type() != null)
            existing.setHost_type(increment.getHost_type());
    }

}
