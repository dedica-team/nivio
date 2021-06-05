package de.bonndan.nivio.input.http;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@Component
public class HttpService {

    public String get(@NonNull final URL url) throws IOException, URISyntaxException {
        return executeRequest(new HttpGet(url.toURI()));
    }

    /**
     * @param url
     * @throws URISyntaxException
     */
    public CachedResponse getResponse(@NonNull final URL url) throws URISyntaxException {

        HttpGet request = new HttpGet(url.toURI());
        request.setHeader(new BasicHeader("Pragma", "no-cache"));
        request.setHeader(new BasicHeader("Cache-Control", "no-cache"));

        try (CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 400) {
                return new CachedResponse(response.getAllHeaders(), response.getEntity());
            } else {
                throw new RuntimeException("Failed to fetch " + request.getURI() + ", status: " + statusCode);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to fetch " + request.getURI(), ex);
        }

    }

    public String getWithBasicAuth(@NonNull final URL url, String username, String password) throws IOException, AuthenticationException, URISyntaxException {
        HttpGet request = new HttpGet(url.toURI());

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
        request.addHeader(new BasicScheme().authenticate(creds, request, null));

        return executeRequest(request);
    }

    public String getWithHeaderToken(@NonNull final URL url, String tokenName, String tokenValue) throws IOException, URISyntaxException {
        HttpGet request = new HttpGet(url.toURI());
        request.addHeader(new BasicHeader(tokenName, tokenValue));

        return executeRequest(request);
    }

    private String executeRequest(HttpGet request) {
        try (CloseableHttpClient client = HttpClients.createDefault(); CloseableHttpResponse response = client.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
            throw new RuntimeException(String.format("Got %d while reading", response.getStatusLine().getStatusCode()));
        } catch (IOException ex) {
            throw new RuntimeException(String.format("Failed to fetch %s", request.getURI()), ex);
        }
    }

}
