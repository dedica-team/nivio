package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.util.URIHelper;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Examines the labels of an item for parts that might be an url or parts of it and could point to targets in the landscape.
 *
 * Every label is split into parts and these parts are matched against landscape item names or identifiers.
 */
public class MagicLabelRelationProcessor extends Processor {

    public static final String KEY_SEPARATOR = "_";

    /**
     * this could be made configurable later
     */
    private static final List<String> URL_PARTS = Arrays.asList("uri", "url", "host");

    protected MagicLabelRelationProcessor(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void process(LandscapeDescription input, Landscape landscape) {

        List<Function<String, Boolean>> blacklistSpecs = getBlacklistSpecs(landscape.getConfig().getLabelBlacklist());

        for (ItemDescription description : input.getItemDescriptions().all()) {
            getHints(description, blacklistSpecs).forEach(hint -> hint.extendLandscape(landscape, processLog));
        }
    }

    private List<Hint> getHints(
            ItemDescription itemDescription,
            List<Function<String, Boolean>> blacklistSpecs
    ) {
        return itemDescription.getLabels().entrySet().stream()
                //skip the blacklisted labels
                .filter(entry -> blacklistSpecs.stream().noneMatch(spec -> spec.apply(entry.getKey())))
                .map(entry -> getHintForLabel(entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Hint getHintForLabel(String key, String value) {

        List<String> keyParts = Arrays.stream(key.split(KEY_SEPARATOR))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        boolean hasUrlLikeKey = URL_PARTS.stream().anyMatch(keyParts::contains);
        Optional<URI> optionalURI = URIHelper.getURI(value);

        if (!hasUrlLikeKey && optionalURI.isEmpty()) {
            return null;
        }

        Hint hint = new Hint();
        hint.setLabel(key);
        optionalURI.ifPresent(hint::setUri);

        return hint;
    }

    private List<Function<String, Boolean>> getBlacklistSpecs(List<String> blacklist) {
        return blacklist.stream().map(s -> {
            try {
                Pattern p = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                return (Function<String, Boolean>) s1 -> p.matcher(s1).matches();
            } catch (Exception e) {
                processLog.warn("Failed to compile group matcher pattern " + s);
                return (Function<String, Boolean>) s1 -> s1.contains(s);
            }

        }).collect(Collectors.toList());
    }
}
