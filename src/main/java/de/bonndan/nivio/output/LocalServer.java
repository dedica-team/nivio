package de.bonndan.nivio.output;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LocalServer implements EnvironmentAware {

    private static Environment env;

    public static String host() {
        return "localhost";
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
