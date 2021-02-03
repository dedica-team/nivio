package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.ReadingException;
import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MapStyleSheetFactory {

    private final FileFetcher fileFetcher;

    public MapStyleSheetFactory(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;
    }

    /**
     * Returns the content of the stylesheet referenced in the config.
     *
     * @param landscapeConfig config containing the stylesheet reference
     * @param processLog      current process log
     * @return css or empty string
     */
    @NonNull
    public String getMapStylesheet(LandscapeConfig landscapeConfig, @NonNull ProcessLog processLog) {

        String mapStylesheet = landscapeConfig.getBranding().getMapStylesheet();
        if (StringUtils.isEmpty(mapStylesheet)) {
            return "";
        }

        try {
            processLog.debug("Loading customer stylesheet: " + mapStylesheet);
            return URLHelper.getURL(mapStylesheet).map(url -> fileFetcher.get(url)).orElse("");
        } catch (ReadingException e) {
            processLog.warn("Failed to load customer stylesheet " + mapStylesheet + ": " + e.getMessage());
            return "";
        }
    }
}
