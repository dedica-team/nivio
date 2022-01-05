package de.bonndan.nivio.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

@Controller
public class LoginController {

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomOAuth2User> whoAmI(OAuth2AuthenticationToken token) {
        if (token != null) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) token.getPrincipal();
            return ResponseEntity.of(Optional.ofNullable(customOAuth2User));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/login")
    public String showLoginPage() {
        return "login";
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/logout")
    public String showLogoutPage() {
        return "logout";
    }

}