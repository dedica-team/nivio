package de.bonndan.nivio.input.external.github;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GitHubConfigTest {

    @Test
    void returnsNullIfNoConfig() {

        GitHubProperties gitHubProperties = new GitHubProperties("kohsuke","012345678","4d98173f7c075527cb64878561d1fe70","my_jwt_token");
        GitHubConfig gitHubConfig = new GitHubConfig(gitHubProperties);
        assertNull(gitHubConfig.getGitHub());
    }

    @Test
    void groupedAssertions(){
        GitHubProperties gitHubProperties = new GitHubProperties("kohsuke","012345678","4d98173f7c075527cb64878561d1fe70","my_jwt_token");
        // in a grouped assertion all assertions are executed, and any failures will be reported together
        assertAll("gitHubProperties",()-> assertEquals("kohsuke",gitHubProperties.getLogin()),
                ()->assertEquals("012345678",gitHubProperties.getPassword()),
                ()-> assertEquals("4d98173f7c075527cb64878561d1fe70",gitHubProperties.getOauth()),
                ()-> assertEquals("my_jwt_token",gitHubProperties.getJwt()));
        gitHubProperties.setLogin("dedicaTest");
        gitHubProperties.setPassword("123456789");
        gitHubProperties.setOauth("abcdef12345");
        gitHubProperties.setJwt("my_new_jwt");

       assertEquals("123456789",gitHubProperties.getPassword());
       assertEquals("abcdef12345",gitHubProperties.getOauth());
       assertEquals("my_new_jwt",gitHubProperties.getJwt());
       assertEquals("dedicaTest",gitHubProperties.getLogin());


    }
    @Test
    void dependentAssertions(){
        // within a code block, if an assertion fails the subsequent code in the
        // same block will be skipped
        GitHubProperties gitHubProperties = new GitHubProperties("kohsuke","012345678","4d98173f7c075527cb64878561d1fe70","my_jwt_token");
        assertAll("properties",
                ()->{String login = gitHubProperties.getLogin();
        assertNotNull(login);
        // executed only if the previous assertion is valid
                    assertAll("login",
                            ()-> assertTrue(login.startsWith("ko")),
                            ()-> assertTrue(login.endsWith("ke")));
        },
                ()->{String password = gitHubProperties.getPassword();
            assertNotNull(password);
            // executed only if the previous assertion is valid
                    assertAll("password",
                            ()-> assertTrue(password.contains("45")),
                            ()-> assertEquals(9,password.length()));

                },
                ()->{String oauth = gitHubProperties.getOauth();
            assertNotNull(oauth);
            // executed only if the previous assertion is valid
                    assertAll("oauth",
                            ()-> assertTrue(oauth.contains("cb648")),
                            ()->assertTrue(oauth.endsWith("0")));
                    },
                ()->{String jwt = gitHubProperties.getJwt();
            assertNotNull(jwt);
            // executed only if the previous assertion is valid
                    assertAll("jwt",
                            ()->assertEquals(12,jwt.length()),
                            ()->assertTrue(jwt.endsWith("ken"))
        );

        }

        );

    }

}