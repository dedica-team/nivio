package de.bonndan.nivio.input.external.gitlab;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.external.ExternalLinkHandler;
import de.bonndan.nivio.input.external.RepositoryLinkHandler;
import de.bonndan.nivio.model.Link;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;

/**
 * Loads the open issues, open pull requests, the readme and the repositories description.
 *
 * You need a mighty "api" scope token, see https://gitlab.com/gitlab-org/gitlab/-/issues/28324 (read only is not sufficient)
 */
public class GitLabRepoHandler implements ExternalLinkHandler, RepositoryLinkHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitLabRepoHandler.class);

    private final GitLabApi api;

    public GitLabRepoHandler(@Nullable GitLabApi api) {
        this.api = api;
    }

    /**
     * Loads the open issues from the repo.
     */
    @Override
    public CompletableFuture<ComponentDescription> resolve(Link link) {
        if (api == null) {
            return CompletableFuture.failedFuture(new RuntimeException("The GitLab API is not configured properly."));
        }

        String repoName = getRepoName(link);
        ItemDescription itemDescription = new ItemDescription();
        try {
            Project project = api.getProjectApi().getProject(repoName);

            String description = project.getDescription();
            itemDescription.setLabel(RepositoryLinkHandler.DESCRIPTION, description);
            itemDescription.setDescription(description);

            itemDescription.setLabel(RepositoryLinkHandler.OPEN_ISSUES, project.getOpenIssuesCount());

            String avatarUrl = project.getAvatarUrl();
            if (!StringUtils.isEmpty(avatarUrl)) {
                itemDescription.setLabel(RepositoryLinkHandler.ICON, avatarUrl);
                itemDescription.setIcon(avatarUrl);
            }

            int size = api.getMergeRequestApi().getMergeRequests(repoName).size();
            itemDescription.setLabel(RepositoryLinkHandler.OPEN_PRS, size);

            //TODO link collaborators
        } catch (Exception e) {
            LOGGER.warn("Failed to grab GitLab data from repo:" + e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.completedFuture(itemDescription);
    }
}
