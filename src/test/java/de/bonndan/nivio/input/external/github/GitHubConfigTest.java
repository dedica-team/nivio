package de.bonndan.nivio.input.external.github;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GitHubConfigTest {

    @Test
    void returnsNullIfNoConfig() {
        GitHubConfig gitHubConfig = new GitHubConfig();
        assertNull(gitHubConfig.getGitHub());
    }
}