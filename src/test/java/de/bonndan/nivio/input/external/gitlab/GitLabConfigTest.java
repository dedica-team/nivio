package de.bonndan.nivio.input.external.gitlab;

import org.junit.jupiter.api.Test;
import java.util.Optional;

import static java.util.Optional.*;
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

}