package de.bonndan.nivio.input.linked;

import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Link;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * https://github-api.kohsuke.org/index.html
 */
public class GitHubProjectHandler implements ExternalLinkHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubProjectHandler.class);
    public static final String OPEN_ISSUES = "github.issues.open";

    private final GitHub gitHub;

    public GitHubProjectHandler(GitHub gitHub) {
        this.gitHub = gitHub;
    }

    /**
     *
     */
    @Override
    public CompletableFuture<String> resolveAndApplyData(Link link, Labeled component) {
        String repoName = getRepoName(link);
        try {
            int openIssues = gitHub.getRepository(repoName).getOpenIssueCount();
            component.setLabel(OPEN_ISSUES, String.valueOf(openIssues));
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect: " + e.getMessage());
        }

        return CompletableFuture.completedFuture("Resolved GitHub project " + repoName);
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
