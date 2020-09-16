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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@Component
public class HttpService {


    public String get(URL url) throws IOException, URISyntaxException {
        CloseableHttpClient client = HttpClients.createDefault();
        return executeRequest(client, new HttpGet(url.toURI()));
    }

    /**
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public CachedResponse getResponse(URL url) throws URISyntaxException {

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

    public String getWithBasicAuth(URL url, String username, String password) throws IOException, AuthenticationException, URISyntaxException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url.toURI());

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
        request.addHeader(new BasicScheme().authenticate(creds, request, null));

        return executeRequest(client, request);
    }

    public String getWithHeaderToken(URL url, String tokenName, String tokenValue) throws IOException, URISyntaxException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url.toURI());
        request.addHeader(new BasicHeader(tokenName, tokenValue));

        return executeRequest(client, request);
    }

    private String executeRequest(CloseableHttpClient client, HttpGet request) throws IOException {
        try (CloseableHttpResponse response = client.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            } else {
                throw new RuntimeException("Got " + response.getStatusLine().getStatusCode() + " while reading");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to fetch " + request.getURI(), ex);
        } finally {
            client.close();
        }
    }

}
