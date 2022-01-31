package de.bonndan.nivio.security;

import de.bonndan.nivio.appuser.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@DataJpaTest
class Oauth2LoginEventAndListenerTest {

    private OAuth2User oAuth2User;

    private final String name = "Mary";
    private final String login = "foo";
    private final String avatarUrl = "https://www.avatar.com";
    private final String externalId = "123";
    private Collection<OAuth2UserAuthority> authorities;
    private CustomOAuth2User customOAuth2User;
    OAuth2LoginEvent oAuth2LoginEvent;
    Oauth2LoginEventListener oauth2LoginEventListener;

    @Autowired
    private AppUserRepository appUserRepository;

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

        oAuth2LoginEvent = new OAuth2LoginEvent(customOAuth2User);
        oauth2LoginEventListener = new Oauth2LoginEventListener(appUserRepository);
    }

    @Test
    void onLogin() {

        // when
        oauth2LoginEventListener.onLogin(oAuth2LoginEvent);

        // then
        assertThat(appUserRepository.findByExternalIdAndIdProvider(customOAuth2User.getExternalId(), customOAuth2User.getIdProvider()).isPresent());
        assertThat(appUserRepository.findByExternalIdAndIdProvider(customOAuth2User.getExternalId(), customOAuth2User.getIdProvider()).equals(customOAuth2User));

    }

    @Test
    void getSource() {

        // then
        assertThat(oAuth2LoginEvent.getSource()).isNotNull();
        assertThat(oAuth2LoginEvent.getSource()).isEqualTo(customOAuth2User);

    }
}
