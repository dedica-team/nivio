package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.observation.InputFormatObserver;
import io.rancher.Rancher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Service
public class InputFormatHandlerRancher1API implements InputFormatHandler {

    public static final String API_SECRET_KEY = "apiSecretKey";
    public static final String API_ACCESS_KEY = "apiAccessKey";

    @Override
    public List<String> getFormats() {
        return Arrays.asList("rancher1");
    }

    @Override
    public void applyData(SourceReference reference, URL baseUrl, LandscapeDescription landscapeDescription) {
        APIWalker apiWalker = new APIWalker(reference, getConfig(reference));
        landscapeDescription.mergeItems(apiWalker.getDescriptions());
    }

    @Override
    public InputFormatObserver getObserver(InputFormatObserver inner, SourceReference reference) {
        Rancher.Config config = getConfig(reference);
        //TODO add observer
        return null;
    }

    private Rancher.Config getConfig(SourceReference reference) {
        Rancher.Config config;
        String accessKey = (String) reference.getProperty(API_ACCESS_KEY);
        String secretKey = (String) reference.getProperty(API_SECRET_KEY);
        if (StringUtils.isEmpty(accessKey)) {
            throw new ProcessingException(reference.getLandscapeDescription(),
                    "Rancher API access key is empty."
            );
        }
        if (accessKey.contains("${")) {
            throw new ProcessingException(reference.getLandscapeDescription(),
                    "Rancher API access key is unresolved: " + accessKey
            );
        }
        if (StringUtils.isEmpty(secretKey)) {
            throw new ProcessingException(reference.getLandscapeDescription(),
                    "Rancher API secret key is empty."
            );
        }
        if (secretKey.contains("${")) {
            throw new ProcessingException(reference.getLandscapeDescription(),
                    "Rancher API secret key is unresolved: " + secretKey
            );
        }

        try {
            config = new Rancher.Config(new URL(reference.getUrl()), accessKey, secretKey);
        } catch (MalformedURLException e) {
            throw new ProcessingException(reference.getLandscapeDescription(), "Could not configure rancher API: " + e.getMessage(), e);
        }

        return config;
    }
}
