package de.bonndan.nivio.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.websocket.DeploymentException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static de.bonndan.nivio.notification.WebSocketConfig.SUBSCRIBE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {"nivio.loginMode=required"},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketsSecuredTest {

    @LocalServerPort
    private Integer port;


    /**
     * https://rieckpil.de/write-integration-tests-for-your-spring-websocket-endpoints/
     */
    @Test
    void securedSockets() throws InterruptedException, ExecutionException, TimeoutException {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue(1);

        var webSocketStompClient = new WebSocketStompClient(new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        webSocketStompClient.setMessageConverter(new StringMessageConverter());

        assertThatThrownBy(() -> {
            StompSession session = webSocketStompClient.connect("ws://localhost:" + port + "/" + SUBSCRIBE,
                            new StompSessionHandlerAdapter() {
                            })
                    .get(1, SECONDS);
        }).isInstanceOf(ExecutionException.class).hasCauseExactlyInstanceOf(DeploymentException.class);
    }
}
