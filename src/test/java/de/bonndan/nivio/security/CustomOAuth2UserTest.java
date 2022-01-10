package de.bonndan.nivio.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class CustomOAuth2UserTest {

    OAuth2User oAuth2User;
    private CustomOAuth2User customOAuth2User;

    private String userName;
    private String avatarUrl;

    Map<String, Object> attributes;

    private Collection<OAuth2UserAuthority> authorities;


    @BeforeEach
    public void setup() {
        oAuth2User = mock(OAuth2User.class);
        customOAuth2User = new CustomOAuth2User(oAuth2User);

        userName = "Mary";
        when(oAuth2User.getAttribute("login")).thenReturn(userName);

        avatarUrl = "https://www.avatar.com";
        when(oAuth2User.getAttribute("avatar_url")).thenReturn(avatarUrl);

        attributes = Map.of(userName, new Object());
        when(oAuth2User.getAttributes()).thenReturn(attributes);

        Map<String, Object> authorityAttributes = Map.of("key", new Object());
        OAuth2UserAuthority grantedAuthority = new OAuth2UserAuthority(authorityAttributes);
        authorities = List.of(grantedAuthority);
        doReturn(authorities).when(oAuth2User).getAuthorities();

    }


    @Test
    void getName() {

        assertThat(customOAuth2User.getName()).isEqualTo(userName);

    }

    @Test
    void getAvatarUrl() {

        assertThat(customOAuth2User.getAvatarUrl()).isEqualTo(avatarUrl);
    }

    @Test
    void getAttributes() {

        assertThat(customOAuth2User.getAttributes()).isEqualTo(attributes);

    }

    @Test
    void getAuthorities() {

        assertThat(customOAuth2User.getAuthorities()).isEqualTo(authorities);

    }

}
