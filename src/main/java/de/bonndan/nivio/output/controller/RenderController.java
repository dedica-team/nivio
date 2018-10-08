package de.bonndan.nivio.output.controller;

import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.LandscapeRepository;
import de.bonndan.nivio.output.dld4e.Dld4eRenderer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping(path = "/render")
public class RenderController {

    private final LandscapeRepository landscapeRepository;

    @Autowired
    public RenderController(LandscapeRepository landscapeRepository) {
        this.landscapeRepository = landscapeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/dld4e/{landscape}")
    public ResponseEntity<String> dld4eResource(@PathVariable(name = "landscape") final String landscapeIdentifier) {
        return new ResponseEntity<>(renderDlde(landscapeIdentifier), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/png/{landscape}")
    public ResponseEntity<String> pngResource(@PathVariable(name = "landscape") final String landscapeIdentifier) throws IOException {

        String payload = renderDlde(landscapeIdentifier);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost("http://localhost:3030");
        request.addHeader(new BasicHeader("Content-Type", "text/yaml"));
        request.setEntity(new StringEntity(payload));
        try (CloseableHttpResponse response = client.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                int tmp;

            } else {
                throw new RuntimeException("Got " + response.getStatusLine().getStatusCode() + " while reading");
            }
            return new ResponseEntity<>("", HttpStatus.OK);
        } finally {
            client.close();
        }
    }

    private String renderDlde(String landscapeIdentifier) {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier);
        if (landscape == null)
            throw new EntityNotFoundException("Not found");

        Dld4eRenderer graphRenderer = new Dld4eRenderer();
        return graphRenderer.render(landscape);
    }


}
