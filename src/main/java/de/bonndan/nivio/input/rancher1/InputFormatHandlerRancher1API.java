package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.SourceReference;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.observation.InputFormatObserver;
import io.rancher.Rancher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class InputFormatHandlerRancher1API implements InputFormatHandler {

    public static final String API_SECRET_KEY = "apiSecretKey";
    public static final String API_ACCESS_KEY = "apiAccessKey";

    @Override
    public List<String> getFormats() {
        return List.of("rancher1");
    }

    @Override
    public List<LandscapeDescription> applyData(@NonNull final SourceReference reference, @NonNull final LandscapeDescription landscapeDescription) {
        APIWalker apiWalker = new APIWalker(reference, getConfig(reference));
        landscapeDescription.mergeItems(apiWalker.getDescriptions());
        return Collections.singletonList(landscapeDescription);
    }

    private Rancher.Config getConfig(SourceReference reference) {
        Rancher.Config config;
        String accessKey = (String) reference.getProperty(API_ACCESS_KEY);
        String secretKey = (String) reference.getProperty(API_SECRET_KEY);
        if (!StringUtils.hasLength(accessKey)) {
            throw new ProcessingException(reference, "Rancher API access key is empty.");
        }
        if (accessKey.contains("${")) {
            throw new ProcessingException(reference, "Rancher API access key is unresolved: " + accessKey);
        }
        if (!StringUtils.hasLength(secretKey)) {
            throw new ProcessingException(reference, "Rancher API secret key is empty.");
        }
        if (secretKey.contains("${")) {
            throw new ProcessingException(reference, "Rancher API secret key is unresolved: " + secretKey);
        }

        config = new Rancher.Config(reference.getUrl(), accessKey, secretKey);

        return config;
    }
}
