package de.bonndan.nivio.input.rancher1;

import de.bonndan.nivio.input.ItemDescriptionFactory;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import org.springframework.stereotype.Service;

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
        APIWalker apiWalker = new APIWalker(reference);
        return apiWalker.getDescriptions();
    }

}
