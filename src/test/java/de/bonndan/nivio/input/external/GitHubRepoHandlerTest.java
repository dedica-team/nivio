package de.bonndan.nivio.input.external;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.external.github.GitHubRepoHandler;
import de.bonndan.nivio.model.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GitHubRepoHandlerTest {

    private GitHubRepoHandler handler;
    private GitHub gitHub;

    @BeforeEach
    void setup() {
        gitHub = mock(GitHub.class);
        handler = new GitHubRepoHandler(gitHub);
    }

    @Test
    void grabsData() throws IOException, ExecutionException, InterruptedException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio"), "github");
        GHRepository ghr = mock(GHRepository.class);
        when(gitHub.getRepository(eq("dedica-team/nivio"))).thenReturn(ghr);
        when(ghr.getOpenIssueCount()).thenReturn(12);
        when(ghr.getDescription()).thenReturn("A description");
        when(ghr.getPullRequests(any())).thenReturn(List.of(mock(GHPullRequest.class)));
        GHContent t = mock(GHContent.class);
        InputStream inputStream = new ByteArrayInputStream("hihi".getBytes());
        when(t.read()).thenReturn(inputStream);
        when(ghr.getReadme()).thenReturn(t);

        //when
        ItemDescription description = (ItemDescription) handler.resolve(link).get();
        verify(gitHub).getRepository(eq("dedica-team/nivio"));

        assertThat(description.getLabel(GitHubRepoHandler.OPEN_ISSUES)).isEqualTo("12");
        assertThat(description.getLabel(GitHubRepoHandler.OPEN_PRS)).isEqualTo("1");
        assertThat(description.getLabel(GitHubRepoHandler.DESCRIPTION)).isEqualTo("A description");
        assertThat(description.getLabel(GitHubRepoHandler.README)).isEqualTo("hihi");
    }

}