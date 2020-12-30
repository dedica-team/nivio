package de.bonndan.nivio.input.external.gitlab;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.external.ExternalLinkHandler;
import de.bonndan.nivio.model.Link;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;

/**
 * Loads the open issues, open pull requests, the readme and the repositories description.
 *
 * https://github-api.kohsuke.org/index.html
 */
public class GitHubRepoHandler implements ExternalLinkHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubRepoHandler.class);

    public static final String OPEN_ISSUES = "github.issues.open";
    public static final String OPEN_PRS = "github.pullrequests.open";
    public static final String README = "github.readme";
    public static final String DESCRIPTION = "github.description";

    private final GitHub gitHub;

    public GitHubRepoHandler(GitHub gitHub) {
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
            GHRepository repository = gitHub.getRepository(repoName);
            int openIssues = repository.getOpenIssueCount();
            itemDescription.setLabel(OPEN_ISSUES, String.valueOf(openIssues));
            int openPRs = repository.getPullRequests(GHIssueState.OPEN).size();
            itemDescription.setLabel(OPEN_PRS, String.valueOf(openPRs));
            String readme = new String(repository.getReadme().read().readNBytes(1024));
            itemDescription.setLabel(README, readme);
            String description = repository.getDescription();
            itemDescription.setLabel(DESCRIPTION, description);
            if (StringUtils.isEmpty(itemDescription.getDescription())) {
                itemDescription.setDescription(description);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to grab GitHub data from repo:" + e.getMessage(), e);
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
