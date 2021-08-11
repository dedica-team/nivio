package de.bonndan.nivio.input.external.gitlab;

import org.gitlab4j.api.GitLabApi;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static java.util.Optional.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GitLabConfigTest {


    @Test
    void returnsNullIfNoConfig()
    {
        GitLabProperties gitLabProperties = new GitLabProperties();
        GitLabConfig gitLabConfig = new GitLabConfig(gitLabProperties);
        assertNull(gitLabConfig.getGitLabAPI());
    }


    @Test
    void groupedAssertions(){
        GitLabProperties gitLabProperties = new GitLabProperties("http://gitlab.example.com","token-string-here123","abracadabra","123456789");
        // in a grouped assertion all assertions are executed, and any failures will be reported together
        assertAll("hostUrl",()->assertEquals("http://gitlab.example.com",gitLabProperties.getHostUrl()),
                ()->assertEquals("token-string-here123",gitLabProperties.getPersonalAccessToken()),
                ()->assertEquals("abracadabra",gitLabProperties.getUsername()),
                ()->assertEquals("123456789",gitLabProperties.getPassword())
        );
        gitLabProperties.setHostUrl("http://gitlab.com");
        gitLabProperties.setPersonalAccessToken("my_access_token");
        gitLabProperties.setUsername("my_account");
        gitLabProperties.setPassword("0123456789");

        assertAll("hostUrl",()->assertEquals("http://gitlab.com",gitLabProperties.getHostUrl()),
                ()->assertEquals("my_access_token",gitLabProperties.getPersonalAccessToken()),
                ()->assertEquals("my_account",gitLabProperties.getUsername()),
                ()->assertEquals("0123456789",gitLabProperties.getPassword())
        );


    }


    @Test
    void dependentAssertions(){
        // with a code block, if an assertion fails the subsequent code in the same block will be skipped
        GitLabProperties gitLabProperties = new GitLabProperties("http://gitlab.example.com","token-string-here123","abracadabra","123456789");
        assertAll("properties",
                ()->{ String hostUrl = gitLabProperties.getHostUrl();
            assertNotNull(hostUrl);
            // executed only if the previous assertion is valid
                    assertAll("Host Url",
                            ()->assertFalse(hostUrl.isEmpty()),
                            ()->assertTrue(hostUrl.contains("exa")));
                    },
                ()->{String personalAccessToken = gitLabProperties.getPersonalAccessToken();
            assertNotNull(personalAccessToken);
            // executed only if the previous assertion is valid
                    assertAll("Personal Access Token",
                            ()->assertTrue(personalAccessToken.endsWith("123")),
                            ()->assertTrue(personalAccessToken.startsWith("tok")));
                    },
                ()->{String username = gitLabProperties.getUsername();
            assertNotNull(username);
            // executed only if the previous assertion is valid
                    assertAll("username",
                            ()->assertTrue(username.contains("dabra")),
                            ()->assertFalse(username.isEmpty()));
                    },
                ()->{String password = gitLabProperties.getPassword();
            assertNotNull(password);
            // executed only if the previous assertion is valid
                    assertAll("password",
                            ()->assertFalse(password.contains("xyz")),
                            ()->assertTrue(password.endsWith("89")));
                     }

           );
    }
    @Test
    void getGitLabAPI(){
        GitLabProperties gitLabProperties = new GitLabProperties("","token-string-here123","abracadabra","123456789");
        GitLabConfig gitLabConfig = new GitLabConfig(gitLabProperties);
        GitLabApi gitLabApi = gitLabConfig.getGitLabAPI();
        assertNotNull(gitLabApi);
        assertFalse(gitLabApi == null);
        assertTrue(gitLabProperties.getHostUrl().isEmpty());
    }

    @Test
    void testGetGitLabAPI(){
        GitLabProperties gitLabProperties = new GitLabProperties("http://gitlab.example.com","token-string-here123","abracadabra","123456789");
        GitLabConfig gitLabConfig = new GitLabConfig(gitLabProperties);
        String hostUrl = gitLabProperties.getHostUrl();
        Optional<String> personalAccessToken = Optional.ofNullable(gitLabProperties.getPersonalAccessToken());
        Optional<String> username = Optional.ofNullable(gitLabProperties.getUsername());
        Optional<String> password = Optional.ofNullable(gitLabProperties.getPassword());

        GitLabApi gitLabApi = gitLabConfig.getGitLabAPI(hostUrl,personalAccessToken,username,password);
        assertNotNull(gitLabApi);
        assertFalse(gitLabApi == null);
        assertThat(hostUrl.isEmpty());
        assertThat(personalAccessToken.isPresent());
        assertThat(username.isPresent());
        assertThat(password.isPresent());
    }


}