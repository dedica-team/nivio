package de.bonndan.nivio.security;

import de.bonndan.nivio.assessment.AssessmentController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(path = AssessmentController.PATH)
public class UserAuthenticationController {
    public static final String PATH = "/user";

//    @GetMapping("/")
//    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
//        return Collections.singletonMap("name", principal.getAttribute("name"));
//    }

}
