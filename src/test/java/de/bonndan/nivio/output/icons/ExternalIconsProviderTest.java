package de.bonndan.nivio.output.icons;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExternalIconsProviderTest {

    @Autowired
    ExternalIconsProvider externalIconsProvider;

    @Test
    @Order(1)
    void getUrls() {
        var testMap = Map.of("apachehttpd", "http://www.apache.org/logos/res/httpd/httpd.png",
                "k8s", "https://raw.githubusercontent.com/kubernetes/kubernetes/master/logo/logo.png",
                "kubernetes", "https://raw.githubusercontent.com/kubernetes/kubernetes/master/logo/logo.png",
                "prometheus", "https://raw.githubusercontent.com/prometheus/docs/master/static/prometheus_logo.png",
                "redhatkeycloak", "https://raw.githubusercontent.com/keycloak/keycloak-misc/master/logo/keycloak_icon_256px.png",
                "redis", "https://redis.io/images/redis-white.png");
        assertThat(externalIconsProvider.getUrls()).isEqualTo(testMap);
    }


    @Test
    @Order(2)
    void setUrls() {
        var testMap = Map.of("testKey", "testValue");
        externalIconsProvider.setUrls(testMap);
        assertThat(externalIconsProvider.getUrls()).isEqualTo(testMap);
    }
}