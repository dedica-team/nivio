package de.bonndan.nivio.output;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;

@Component
public class LocalServer implements EnvironmentAware {

    private static Environment env;

    /**
     * without slash
     */
    private final String baseUrl;

    public LocalServer(@Value("${nivio.baseUrl:}") String baseUrl) {
        if (!StringUtils.isEmpty(baseUrl)) {
            this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(1) : baseUrl;
        } else {
            this.baseUrl = "http://" + host() + ":" + port();
        }
    }

    /**
     * Returns the current publically visible url.
     *
     * @param path path to add
     * @return url with host, port
     */
    public String getUrl(String path) {
        return baseUrl + (path.startsWith("/") ? path : "/" + path);
    }

    private static String host() {
        return InetAddress.getLoopbackAddress().getHostName();
    }

    private static String port() {

        if (env != null) {
            String port = env.getProperty("local.server.port");
            if (port != null && Integer.valueOf(port) != 0)
                return port;
        }

        return "8080";
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }
}
