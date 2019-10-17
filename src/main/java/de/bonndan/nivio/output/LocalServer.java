package de.bonndan.nivio.output;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class LocalServer implements EnvironmentAware {

    private static Environment env;

    public static String host() {
        return InetAddress.getLoopbackAddress().getHostName();
    }

    public static String port() {

        if (env != null) {
            String port = env.getProperty("local.server.port");
            if (port != null && Integer.valueOf(port) != 0)
                return port;
        }

        return "8080";
    }

    public static String url(String path) {
        return "http://" + host() + ":" + port() + path;
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }
}
