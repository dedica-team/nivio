package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.ItemDescriptionFactory;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import io.rancher.Rancher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Service
public class ItemDescriptionFactoryRancher1API implements ItemDescriptionFactory {

    public static final String API_SECRET_KEY = "apiSecretKey";
    public static final String API_ACCESS_KEY = "apiAccessKey";

    @Override
    public List<String> getFormats() {
        return Arrays.asList("rancher1");
    }

    @Override
    public List<ItemDescription> getDescriptions(SourceReference reference, URL baseUrl) {
        APIWalker apiWalker = new APIWalker(reference, getConfig(reference));
        return apiWalker.getDescriptions();
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
            String[] keys = System.getenv().keySet().toArray(String[]::new);
            throw new ProcessingException(reference.getLandscapeDescription(),
                    "Rancher API access key is unresolved: " + accessKey + ", picked up env vars: " + StringUtils.arrayToCommaDelimitedString(keys)
            );
        }
        if (StringUtils.isEmpty(secretKey)) {
            throw new ProcessingException(reference.getLandscapeDescription(),
                    "Rancher API secret key is empty."
            );
        }
        if (secretKey.contains("${")) {
            String[] keys = System.getenv().keySet().toArray(String[]::new);
            throw new ProcessingException(reference.getLandscapeDescription(),
                    "Rancher API secret key is unresolved: " + secretKey + ", picked up env vars: " + StringUtils.arrayToCommaDelimitedString(keys)
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
