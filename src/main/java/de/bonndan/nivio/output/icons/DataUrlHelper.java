package de.bonndan.nivio.output.icons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

public class DataUrlHelper {

    public static final String DATA_IMAGE_SVG_XML_BASE_64 = "data:image/svg+xml;base64,";
    public static final String DATA_IMAGE_PNG_XML_BASE_64 = "data:image/png;base64,";
    public static final String DATA_IMAGE_JPEG_XML_BASE_64 = "data:image/jpeg;base64,";

    private static final Logger LOGGER = LoggerFactory.getLogger(DataUrlHelper.class);


    /**
     * Returns the base64 encoded content of the resource behind the path
     *
     * @param path filesystem location
     * @return base64 encoded bytes
     */
    public static Optional<String> asBase64(String path) {

        try (InputStream resourceAsStream = DataUrlHelper.class.getResourceAsStream(path)) {
            if (resourceAsStream == null) throw new RuntimeException("File does not exist or is empty.");
            byte[] bytes = StreamUtils.copyToByteArray(resourceAsStream);
            return asBase64(bytes);
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("Failed to load icon {}", path, e);
            return Optional.empty();
        }
    }

    public static Optional<String> asBase64(byte[] bytes) {
        if (bytes.length == 0) {
            throw new IllegalArgumentException("Zero length bytes given");
        }
        return Optional.of(Base64.getEncoder().encodeToString(bytes));
    }
}
