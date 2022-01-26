package de.bonndan.nivio.security;

import de.bonndan.nivio.appuser.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DataJpaTest
class CustomOAuth2UserServiceTest {

    private OAuth2User oAuth2User;

    private String name = "Mary";
    private String login = "foo";
    private String avatarUrl = "https://www.avatar.com";
    private String externalId = "123";
    private String idp = "github";
    private Collection<OAuth2UserAuthority> authorities;
    private CustomOAuth2User customOAuth2User;
    private ApplicationEventPublisher applicationEventPublisher;


    @Autowired
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

    }

    @Test
    void fromGitHubUser() {

        // when
        when(oAuth2User.getAttribute("name")).thenReturn(name);
        customOAuth2User = CustomOAuth2UserService.fromGitHubUser(oAuth2User, "login", "name");

        // then
        assertThat(customOAuth2User).isNotNull();
        assertThat(customOAuth2User.getAlias()).isEqualTo(login);
        assertThat(customOAuth2User.getExternalId()).isEqualTo(externalId);
        assertThat(customOAuth2User.getName()).isEqualTo((name));
    }

    @Test
    void fromGitHubUserWithMissingNameFallsBackToLogin() {

        // when
        when(oAuth2User.getAttribute("name")).thenReturn(null);
        customOAuth2User = CustomOAuth2UserService.fromGitHubUser(oAuth2User, "login", "name");

        // then
        assertThat(customOAuth2User.getName()).isEqualTo(login);
    }

    @Test
    void loadUser() {

        // given
        AuthConfigProperties authConfigProperties = new AuthConfigProperties();
        OAuth2UserRequest userRequest;
        userRequest = mock(OAuth2UserRequest.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        CustomOAuth2UserService customOAuth2UserService = new CustomOAuth2UserService(authConfigProperties, applicationEventPublisher);

        // when
        customOAuth2UserService.loadUser(userRequest);


        // then


//        customOAuth2User = CustomOAuth2UserService.fromGitHubUser(oAuth2User, "login", "name");

//        AppUser appUser = new AppUser();

//        appUser.setName(customOAuth2User.getName());
//        appUser.setAlias(customOAuth2User.getAlias());
//        appUser.setAvatarUrl(customOAuth2User.getAvatarUrl());
//        appUser.setAppUserRole(AppUserRole.USER);
//        appUser.setLocked(false);
//        appUser.setEnabled(true);
//        appUser.setExternalId(customOAuth2User.getExternalId());
//        appUser.setIdp(customOAuth2User.getIdp());

        // then
//        assertThat(appUser.getName()).isEqualTo(name);
//        assertThat(appUser.getAlias()).isEqualTo(login);
//        assertThat(appUser.getAvatarUrl()).isEqualTo(avatarUrl);
//        assertThat(appUser.getAppUserRole()).isEqualTo(AppUserRole.USER);
//        assertThat(appUser.getLocked()).isFalse();
//        assertThat(appUser.getEnabled()).isTrue();
//        assertThat(appUser.getExternalId()).isEqualTo(externalId);
//        assertThat(appUser.getIdp()).isEqualTo(idp);

    }

}
