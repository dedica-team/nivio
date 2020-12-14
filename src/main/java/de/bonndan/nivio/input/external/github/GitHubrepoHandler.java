package de.bonndan.nivio.input.external.github;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.external.ExternalLinkHandler;
import de.bonndan.nivio.model.Link;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;

/**
 * https://github-api.kohsuke.org/index.html
 */
public class GitHubrepoHandler implements ExternalLinkHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubrepoHandler.class);
    public static final String OPEN_ISSUES = "github.issues.open";

    private final GitHub gitHub;

    public GitHubrepoHandler(GitHub gitHub) {
        this.gitHub = gitHub;
    }

    /**
     * Loads the open issues from the repo.
     */
    @Override
    public CompletableFuture<ComponentDescription> resolve(Link link) {
        String repoName = getRepoName(link);
        ItemDescription itemDescription = new ItemDescription();
        try {
            int openIssues = gitHub.getRepository(repoName).getOpenIssueCount();
            itemDescription.setLabel(OPEN_ISSUES, String.valueOf(openIssues));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.completedFuture(itemDescription);
    }

    @NonNull
    private String getRepoName(Link link) {
        try {
            String s = link.getHref().toString().split(link.getHref().getHost())[1];
            s = StringUtils.trimTrailingCharacter(s, '/');
            return StringUtils.trimLeadingCharacter(s, '/');
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse github url " + link.getHref() + ": " + e.getMessage());
        }

    }
}
