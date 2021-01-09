package de.bonndan.nivio.input.external;

import de.bonndan.nivio.model.Link;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

public interface RepositoryLinkHandler {

    String OPEN_ISSUES = "repo.issues.open";
    String OPEN_PRS = "repo.pullrequests.open";
    String README = "repo.readme";
    String DESCRIPTION = "repo.description";
    String ICON = "repo.icon";

    @NonNull
    default String getRepoName(Link link) {
        try {
            String s = link.getHref().toString().split(link.getHref().getHost())[1];
            s = StringUtils.trimTrailingCharacter(s, '/');
            return StringUtils.trimLeadingCharacter(s, '/');
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to parse repository url %s: %s", link.getHref(), e.getMessage()));
        }

    }
}
