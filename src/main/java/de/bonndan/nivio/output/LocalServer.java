package de.bonndan.nivio.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Representation of the running service.
 *
 *
 */
@Service
public class LocalServer implements EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalServer.class);

    private static Environment env;

    /**
     * without slash
     */
    private final String baseUrl;

    public LocalServer(@Value("${nivio.baseUrl:}") String baseUrl) {
        if (!StringUtils.isEmpty(baseUrl)) {
            this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        } else {
            this.baseUrl = "http://" + host() + ":" + port();
        }
    }

    private static String host() {
        return InetAddress.getLoopbackAddress().getHostName();
    }

    private static String port() {

        if (env != null) {
            String port = env.getProperty("local.server.port");
            if (port != null && Integer.parseInt(port) != 0) {
                return port;
            }
        }

        return "8080";
    }

    /**
     * Returns the visible url.
     *
     * @param path path to add
     * @return url with host, port
     */
    public Optional<URL> getUrl(String path) {
        try {
            return Optional.of(new URL(baseUrl + (path.startsWith("/") ? path : "/" + path)));
        } catch (MalformedURLException ignored) {
            LOGGER.warn("Failed to build url for {}", path);
            return Optional.empty();
        }
    }

    /**
     * Returns the visible url.
     *
     * @param parts path to add, concatenated by "/"
     * @return url with host, port
     */
    public Optional<URL> getUrl(String... parts) {
        return getUrl(StringUtils.arrayToDelimitedString(parts, "/"));
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }
}
