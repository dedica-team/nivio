package de.bonndan.nivio.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

@Controller
public class LoginController {

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/user")
    public ResponseEntity<String> whoAmI(OAuth2AuthenticationToken principal) {
        if (principal != null) {
            return ResponseEntity.of(Optional.ofNullable(principal.getPrincipal().getAttribute("login")));
        } else {
            return ResponseEntity.of(Optional.of("anonymous"));
        }
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @GetMapping(path = "/login")
    public String showLoginPage() {
        return "login";
    }

}