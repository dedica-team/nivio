package de.bonndan.nivio.input.external;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.external.github.GitHubRepoHandler;
import de.bonndan.nivio.input.external.gitlab.GitLabConfig;
import de.bonndan.nivio.input.external.gitlab.GitLabProperties;
import de.bonndan.nivio.input.external.gitlab.GitLabRepoHandler;
import de.bonndan.nivio.model.Link;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GitLabRepoHandlerTest {

    private GitLabRepoHandler handler;
    private GitLabApi gitLabAPI;
    private RepositoryApi repoApi;
    private ProjectApi projectApi;
    private MergeRequestApi mrApi;

    @BeforeEach
    void setup() {
        gitLabAPI = mock(GitLabApi.class);
        repoApi = mock(RepositoryApi.class);
        when(gitLabAPI.getRepositoryApi()).thenReturn(repoApi);

        projectApi = mock(ProjectApi.class);
        when(gitLabAPI.getProjectApi()).thenReturn(projectApi);

        mrApi = mock(MergeRequestApi.class);
        when(gitLabAPI.getMergeRequestApi()).thenReturn(mrApi);

        handler = new GitLabRepoHandler(gitLabAPI);
    }

    @Test
    void setsLabels() throws MalformedURLException, ExecutionException, InterruptedException, GitLabApiException {
        //given
        Link link = new Link(new URL("https://gitlab.com/bonndan/nivio-private-demo"), "gitlab");

        Project project = new Project();
        project.setDescription("Private repo test");
        project.setOpenIssuesCount(12);
        when(projectApi.getProject(eq("bonndan/nivio-private-demo"))).thenReturn(project);

        when(mrApi.getMergeRequests(eq("bonndan/nivio-private-demo"))).thenReturn(List.of(mock(MergeRequest.class)));

        //when
        ItemDescription description = (ItemDescription) handler.resolve(link).get();

        //then
        assertThat(description.getLabel(RepositoryLinkHandler.DESCRIPTION)).isEqualTo("Private repo test");
        assertThat(description.getLabel(RepositoryLinkHandler.OPEN_ISSUES)).isEqualTo("12");
    }

    @Test
    void setsIconFromAvatar() throws GitLabApiException, MalformedURLException, ExecutionException, InterruptedException {
        //given
        Link link = new Link(new URL("https://gitlab.com/bonndan/nivio-private-demo"), "gitlab");

        Project project = new Project();
        String avatarUrl = "https://foo.bar.com/logo.jpg";
        project.setAvatarUrl(avatarUrl);
        when(projectApi.getProject(eq("bonndan/nivio-private-demo"))).thenReturn(project);
        when(mrApi.getMergeRequests(eq("bonndan/nivio-private-demo"))).thenReturn(List.of(mock(MergeRequest.class)));

        //when
        ItemDescription description = (ItemDescription) handler.resolve(link).get();

        assertThat(description.getIcon()).isEqualTo(avatarUrl);
    }


    @Test
    void setsDescriptionIfEmpty() throws IOException, ExecutionException, InterruptedException {

    }

    @Disabled
    @Test
    /** add two new variables and initialize it */
    void integration() throws IOException, ExecutionException, InterruptedException {

        //env vars used, configure them properly
        GitLabProperties gitLabProperties = null;
        GitLabConfig gitLabConfig = new GitLabConfig(null);
        GitLabApi gitLabAPI = gitLabConfig.getGitLabAPI();

        handler = new GitLabRepoHandler(gitLabAPI);

        //given
        Link link = new Link(new URL("https://gitlab.com/bonndan/nivio-private-demo"), "gitlab");

        //when
        ItemDescription description = (ItemDescription) handler.resolve(link).get();

        assertThat(description.getLabel(GitHubRepoHandler.DESCRIPTION)).isEqualTo("Private repo test");
    }
}