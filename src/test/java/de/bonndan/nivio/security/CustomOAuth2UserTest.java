package de.bonndan.nivio.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class CustomOAuth2UserTest {

    OAuth2User oAuth2User;
    private CustomOAuth2User customOAuth2User;

    private String userName = "Mary";
    private String login = "foo";
    private String avatarUrl = "https://www.avatar.com";
    private String email = "email";
    String idp = "github";
    String externalId = "123";

    private Collection<OAuth2UserAuthority> authorities;


    @BeforeEach
    public void setup() {
        oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("login")).thenReturn(login);
        when(oAuth2User.getAttribute("name")).thenReturn(userName);
        when(oAuth2User.getAttribute("avatar_url")).thenReturn(avatarUrl);
        when(oAuth2User.getAttribute("email")).thenReturn(email);
        when(oAuth2User.getAttribute("id")).thenReturn(externalId);
        when(oAuth2User.getAttributes()).thenReturn(Map.of());

        Map<String, Object> authorityAttributes = Map.of("key", new Object());
        OAuth2UserAuthority grantedAuthority = new OAuth2UserAuthority(authorityAttributes);
        authorities = List.of(grantedAuthority);
        doReturn(authorities).when(oAuth2User).getAuthorities();

        customOAuth2User = CustomOAuth2UserService.fromGitHubUser(oAuth2User, "login", "name");
    }

    @Test
    void getExternalId() {
        assertThat(customOAuth2User.getExternalId()).isEqualTo(externalId);
    }

    @Test
    void getName() {
        assertThat(customOAuth2User.getName()).isEqualTo(userName);
    }

    @Test
    void getLogin() {
        assertThat(customOAuth2User.getAlias()).isEqualTo(login);
    }

    @Test
    void getAvatarUrl() {
        assertThat(customOAuth2User.getAvatarUrl()).isEqualTo(avatarUrl);
    }

    @Test
    void getAttributes() {
        assertThat(customOAuth2User.getAttributes()).isEqualTo(Map.<String, Object>of());
    }

    @Test
    void getAuthorities() {
        assertThat(customOAuth2User.getAuthorities()).isEqualTo(authorities);
    }

    @Test
    void getIdp() { assertThat(customOAuth2User.getIdp()).isEqualTo(idp); }

}