package de.bonndan.nivio.input.external;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.external.github.GitHubrepoHandler;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GitHubrepoHandlerTest {

    private GitHubrepoHandler handler;
    private GitHub gitHub;

    @BeforeEach
    void setup() {
        gitHub = mock(GitHub.class);
        handler = new GitHubrepoHandler(gitHub);
    }

    @Test
    void grabsOpenIssues() throws IOException, ExecutionException, InterruptedException {
        //given
        Link link = new Link(new URL("https://github.com/dedica-team/nivio"), "github");
        GHRepository ghr = mock(GHRepository.class);
        when(gitHub.getRepository(eq("dedica-team/nivio"))).thenReturn(ghr);
        when(ghr.getOpenIssueCount()).thenReturn(12);

        //when
        ItemDescription description = (ItemDescription) handler.resolve(link).get();
        verify(gitHub).getRepository(eq("dedica-team/nivio"));

        assertThat(description.getLabel(GitHubrepoHandler.OPEN_ISSUES)).isEqualTo("12");
    }

}