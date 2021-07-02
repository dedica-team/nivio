package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.InterfaceDescription;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ServiceInterfaceTest {

    @Test
    void constructor() throws MalformedURLException {
        InterfaceDescription description = new InterfaceDescription();
        description.setDescription("desc");
        description.setName("name");
        description.setDeprecated(true);
        description.setSummary("summary");
        description.setPayload("payload");
        description.setParameters("one, two");
        description.setProtection("foo");
        description.setFormat("bar");
        description.setUrl(new URL("http://foo.com"));

        ServiceInterface serviceInterface = new ServiceInterface(description);
        assertThat(serviceInterface.getParameters()).isEqualTo(description.getParameters());
        assertThat(serviceInterface.getDescription()).isEqualTo(description.getDescription());
        assertThat(serviceInterface.getPath()).isEqualTo(description.getPath());
        assertThat(serviceInterface.getPayload()).isEqualTo(description.getPayload());
        assertThat(serviceInterface.getSummary()).isEqualTo(description.getSummary());
        assertThat(serviceInterface.getFormat()).isEqualTo(description.getFormat());
        assertThat(serviceInterface.getProtection()).isEqualTo(description.getProtection());
        assertThat(serviceInterface.isDeprecated()).isTrue();

    }
}