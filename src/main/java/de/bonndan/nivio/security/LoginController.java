package de.bonndan.nivio.security;

import de.bonndan.nivio.config.NivioConfigProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

@Controller
public class LoginController {

    private final String loginMode;

    public LoginController(NivioConfigProperties properties) {
        this.loginMode = properties.getLoginMode();
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomOAuth2User> user(OAuth2AuthenticationToken token) {
        if (loginMode.equalsIgnoreCase(SecurityConfig.LOGIN_MODE_NONE)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

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
        if (loginMode.equalsIgnoreCase(SecurityConfig.LOGIN_MODE_REQUIRED)) {
            return "login";
        }

        return "";
    }

}