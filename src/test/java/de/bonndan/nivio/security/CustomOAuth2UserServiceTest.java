package de.bonndan.nivio.security;

import de.bonndan.nivio.appuser.AppUser;
import de.bonndan.nivio.appuser.AppUserRepository;
import de.bonndan.nivio.appuser.AppUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomOAuth2UserServiceTest {

    private OAuth2User oAuth2User;

    private String name = "Mary";
    private String login = "foo";
    private String avatarUrl = "https://www.avatar.com";
    private String externalId = "123";
    private String idp = "github";
    private Collection<OAuth2UserAuthority> authorities;
    private CustomOAuth2User customOAuth2User;

    @MockBean
    AppUserRepository appUserRepository;

    @BeforeEach
    public void setup() {
        oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("login")).thenReturn(login);
        when(oAuth2User.getAttribute("avatar_url")).thenReturn(avatarUrl);
        when(oAuth2User.getAttribute("id")).thenReturn(externalId);
        when(oAuth2User.getAttribute("name")).thenReturn(name);

        when(oAuth2User.getAttributes()).thenReturn(Map.of());

        Map<String, Object> authorityAttributes = Map.of("key", new Object());
        OAuth2UserAuthority grantedAuthority = new OAuth2UserAuthority(authorityAttributes);
        authorities = List.of(grantedAuthority);
        doReturn(authorities).when(oAuth2User).getAuthorities();

        customOAuth2User = CustomOAuth2UserService.fromGitHubUser(oAuth2User, "login", "name");
    }

    @Test
    void fromGitHubUser() {

        //given
        when(oAuth2User.getAttribute("name")).thenReturn(name);

        //then
        assertThat(customOAuth2User).isNotNull();
        assertThat(customOAuth2User.getAlias()).isEqualTo(login);
        assertThat(customOAuth2User.getExternalId()).isEqualTo(externalId);
        assertThat(customOAuth2User.getName()).isEqualTo((name));
    }

    @Test
    void fromGitHubUserWithMissingNameFallsBackToLogin() {

        //given
        when(oAuth2User.getAttribute("name")).thenReturn(null);

        //then
        assertThat(customOAuth2User.getName()).isEqualTo(login);
    }

    @Test
    void saveUser() {

        // given
        AppUser appUser = new AppUser();
        appUser.setName(customOAuth2User.getName());
        appUser.setAlias(customOAuth2User.getAlias());
        appUser.setAvatarUrl(customOAuth2User.getAvatarUrl());
        appUser.setAppUserRole(AppUserRole.USER);
        appUser.setLocked(false);
        appUser.setEnabled(true);
        appUser.setExternalId(customOAuth2User.getExternalId());
        appUser.setIdp(customOAuth2User.getIdp());

        // then
        assertThat(appUser.getName()).isEqualTo(name);
        assertThat(appUser.getAlias()).isEqualTo(login);
        assertThat(appUser.getAvatarUrl()).isEqualTo(avatarUrl);
        assertThat(appUser.getAppUserRole()).isEqualTo(AppUserRole.USER);
        assertThat(appUser.getLocked()).isFalse();
        assertThat(appUser.getEnabled()).isTrue();
        assertThat(appUser.getExternalId()).isEqualTo(externalId);
        assertThat(appUser.getIdp()).isEqualTo(idp);

    }
}