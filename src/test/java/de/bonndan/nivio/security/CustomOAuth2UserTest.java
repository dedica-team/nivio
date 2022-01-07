package de.bonndan.nivio.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class CustomOAuth2UserTest {

    OAuth2User oAuth2User;

    @BeforeEach
    public void setup() {
        oAuth2User = mock(OAuth2User.class);
    }


    @Test
    void getName() {
        // given
        String userName = "Mary";
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2User);

        // when
        when(customOAuth2User.getName()).thenReturn(userName);

        // then
        assertThat(customOAuth2User.getName()).isEqualTo("Mary");
    }

    @Test
    void getAvatarUrl() {
        // given
        String avatarUrl = "https://www.avatar.com";
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2User);

        // when
        when(customOAuth2User.getAvatarUrl()).thenReturn(avatarUrl);

        // then
        assertThat(customOAuth2User.getAvatarUrl()).isEqualTo(avatarUrl);
    }

    @Test
    void getAttributes() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        Object object = new Object();
        attributes.put("Mary", object);
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2User);

        // when
        when(customOAuth2User.getAttributes()).thenReturn(attributes);

        // then
        assertThat(customOAuth2User.getAttributes()).isEqualTo(attributes);
    }

}
