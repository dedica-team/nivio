package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.ReadingException;
import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

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
     * @param processLog current process log
     * @return css or empty string
     */
    public String getMapStylesheet(LandscapeConfig landscapeConfig, @NonNull ProcessLog processLog) {

        String mapStylesheet = landscapeConfig.getBranding().getMapStylesheet();
        if (StringUtils.isEmpty(mapStylesheet)) {
            return "";
        }

        String mapCss = "";
        processLog.debug("Loading customer stylesheet: " + mapStylesheet);
        try {
            URL url = new URL(mapStylesheet);
            if (URLHelper.isLocal(url)) {
                mapCss = Files.readString(new File(url.toString()).toPath());
            } else {
                mapCss = fileFetcher.get(url);
            }
        } catch (IOException | ReadingException e ) {
            processLog.warn("Failed to load customer stylesheet " + mapStylesheet + ": " + e.getMessage());
        }
        return mapCss;
    }
}
