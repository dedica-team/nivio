package de.bonndan.nivio.security;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Service that ensures that {@link CustomOAuth2User} users are used in the application.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthConfigProperties authConfigProperties;

    public CustomOAuth2UserService(AuthConfigProperties authConfigProperties) {
        this.authConfigProperties = authConfigProperties;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        try {
            return fromGitHubUser(user, authConfigProperties.getGithubAliasAttribute(), authConfigProperties.getGithubNameAttribute());
        } catch (NullPointerException e) {
            throw new OAuth2AuthenticationException(String.format("Failed to create custom user: %s", e.getMessage()));
        }
    }

    /**
     * Factory method to create a custom user based on github oauth data.
     *
     * @param user           retrived user
     * @param aliasAttribute attribute for the alias (login)
     * @param nameAttribute  attribute for the name
     * @return custom user
     */
    public static CustomOAuth2User fromGitHubUser(@NonNull final OAuth2User user,
                                                  @Nullable final String aliasAttribute,
                                                  @Nullable final String nameAttribute
    ) {
        return new CustomOAuth2User(
                Optional.ofNullable((String) user.getAttribute("id")).orElse(""),
                StringUtils.hasLength(aliasAttribute) ? Optional.ofNullable((String) user.getAttribute(aliasAttribute)).orElse("") : "",
                StringUtils.hasLength(nameAttribute) ? Optional.ofNullable((String) user.getAttribute(nameAttribute)).orElse("") : "",
                user.getAttributes(),
                user.getAuthorities(),
                user.getAttribute("avatar_url")
        );
    }

}