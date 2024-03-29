package de.bonndan.nivio.security;

import de.bonndan.nivio.api.LinkFactory;
import de.bonndan.nivio.config.NivioConfigProperties;
import de.bonndan.nivio.model.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import java.util.Optional;

import static de.bonndan.nivio.api.LinkFactory.AUTH_LOGIN_GITHUB;

@Controller
public class LoginController {

    private final LinkFactory linkFactory;
    private final NivioConfigProperties properties;
    private final String loginMode;

    public LoginController(LinkFactory linkFactory, NivioConfigProperties properties, AuthConfigProperties authConfigProperties) {
        this.linkFactory = linkFactory;
        this.properties = properties;
        this.loginMode = authConfigProperties.getLoginMode();
    }

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

    /**
     * Login is shown for required and optional, although optional would usually be handled by the frontend.
     */
    @GetMapping(path = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String showLoginPage(Model model, OAuth2AuthenticationToken token) {

        boolean modeMet = SecurityConfig.LOGIN_MODE_REQUIRED.equalsIgnoreCase(loginMode)
                || SecurityConfig.LOGIN_MODE_OPTIONAL.equalsIgnoreCase(loginMode);
        if (!modeMet || token != null) {
            return "redirect:/";
        }

        model.addAttribute("brandingLogoUrl", properties.getBrandingLogoUrl());
        model.addAttribute("brandingForeground", properties.getBrandingForeground());
        model.addAttribute("brandingBackground", properties.getBrandingBackground());
        model.addAttribute("brandingMessage", properties.getBrandingMessage());
        Map<String, Link> authLinks = linkFactory.getAuthLinks();
        if (authLinks.containsKey(AUTH_LOGIN_GITHUB)) {
            model.addAttribute("auth_github", authLinks.get(AUTH_LOGIN_GITHUB));
        }
        return "login";
    }

}
