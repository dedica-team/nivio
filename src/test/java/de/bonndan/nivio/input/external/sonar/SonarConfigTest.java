package de.bonndan.nivio.input.external.sonar;

import de.bonndan.nivio.input.external.gitlab.GitLabProperties;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SonarConfigTest {

    @Test
    void returnNotNullIfConfig(){
        SonarProperties sonarProperties = new SonarProperties("https://www.sonarcloud.io","my_sonar_account","123456789","proxy.sonarcloud.io","8080");
        SonarConfig sonarConfig = new SonarConfig(sonarProperties);
        assertNotNull(sonarConfig.getSonarClientBuilder());

    }

    @Test
    void groupedAssertions(){
        SonarProperties sonarProperties = new SonarProperties("https://www.sonarcloud.io","my_sonar_account","123456789","proxy.sonarcloud.io","8080");
        // in a grouped assertion all assertions are executed, and any failures will be reported together
        assertAll("serverUrl",()->assertEquals("https://www.sonarcloud.io",sonarProperties.getServerUrl()),
                ()->assertEquals("my_sonar_account",sonarProperties.getLogin()),
                ()->assertEquals("123456789",sonarProperties.getPassword()),
                ()->assertEquals("proxy.sonarcloud.io",sonarProperties.getProxyHost()),
                ()->assertEquals("8080",sonarProperties.getProxyPort())
        );
        sonarProperties.setServerUrl("https://www.sonarqube.org");
        sonarProperties.setLogin("my_new_account");
        sonarProperties.setPassword("abracadabra");
        sonarProperties.setProxyHost("proxy.sonarqube.org");
        sonarProperties.setProxyPort("8090");

        assertAll("serverUrl",()->assertEquals("https://www.sonarqube.org",sonarProperties.getServerUrl()),
                ()->assertEquals("my_new_account",sonarProperties.getLogin()),
                ()->assertEquals("abracadabra",sonarProperties.getPassword()),
                ()->assertEquals("proxy.sonarqube.org",sonarProperties.getProxyHost()),
                ()->assertEquals("8090",sonarProperties.getProxyPort())
        );
    }

}
