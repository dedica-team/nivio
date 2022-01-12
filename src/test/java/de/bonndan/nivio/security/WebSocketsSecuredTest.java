package de.bonndan.nivio.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.websocket.DeploymentException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static de.bonndan.nivio.notification.WebSocketConfig.SUBSCRIBE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {"auth.loginMode=required"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketsSecuredTest {

    Integer port = 0;

    @Autowired
    Environment environment;

    @BeforeEach
    void setup() {
        String property = environment.getProperty("local.server.port");
        if (StringUtils.hasLength(property)) {
            port = Integer.valueOf(property);
        }
    }


    /**
     * https://rieckpil.de/write-integration-tests-for-your-spring-websocket-endpoints/
     */
    @Test
    void securedSockets() {

        if (port == 0) {
            //this test does not work in github windows actions
            return;
        }

        var webSocketStompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        webSocketStompClient.setMessageConverter(new StringMessageConverter());

        assertThatThrownBy(() -> {
            webSocketStompClient.connect("ws://localhost:" + port + "/" + SUBSCRIBE,
                            new StompSessionHandlerAdapter() {
                            })
                    .get(1, SECONDS);
        }).isInstanceOf(ExecutionException.class).hasCauseExactlyInstanceOf(DeploymentException.class);
    }
}
