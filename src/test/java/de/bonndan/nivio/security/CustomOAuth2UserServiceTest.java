package de.bonndan.nivio.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class CustomOAuth2UserServiceTest {

    OAuth2AccessToken accessToken;
    DefaultOAuth2UserService defaultOAuth2UserService;
    OAuth2User user;
    private static final String REGISTRATION_ID = "registration-1";
    AuthorizationGrantType authorizationGrantType = new AuthorizationGrantType("value");


    @BeforeEach
    void setup() {

        accessToken = mock(OAuth2AccessToken.class);
//        userRequest = mock(OAuth2UserRequest.class);
        defaultOAuth2UserService = mock(DefaultOAuth2UserService.class);
        user = mock(OAuth2User.class);
    }

//    @Test
//    void loadUser() {
//
//        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(REGISTRATION_ID)
//                .authorizationGrantType(authorizationGrantType)
//                .issuerUri("dajajk")
//                .build();
//
//
//        // given
//        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessToken);
//
//        CustomOAuth2UserService customOAuth2UserService = new CustomOAuth2UserService();
//
////        // when
////        when(defaultOAuth2UserService.loadUser(userRequest)).thenReturn(user);
////        when(userRequest.getClientRegistration()).thenReturn(ClientRegistration.withClientRegistration(clientRegistration).build());
//
//        // then
//        assertThat(customOAuth2UserService.loadUser(userRequest)).isEqualTo(user);
//
//    }


}
