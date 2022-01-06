package de.bonndan.nivio.security;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${nivio.loginType}")
    private String loginType;

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomOAuth2User> whoAmI(OAuth2AuthenticationToken token) {
        if (!loginType.equalsIgnoreCase("none")) {
            if (token != null) {
                CustomOAuth2User customOAuth2User = (CustomOAuth2User) token.getPrincipal();
                return ResponseEntity.of(Optional.ofNullable(customOAuth2User));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/login")
    public String showLoginPage() {
        if (!loginType.equalsIgnoreCase("none") && !loginType.equalsIgnoreCase("optional")) {
            return "login";
        } else {
            return "";
        }
    }


}