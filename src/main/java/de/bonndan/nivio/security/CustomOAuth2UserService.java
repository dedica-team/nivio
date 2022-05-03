package de.bonndan.nivio.security;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * Service that ensures that {@link CustomOAuth2User} users are used in the application.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthConfigProperties authConfigProperties;

    private final ApplicationEventPublisher applicationEventPublisher;

    public CustomOAuth2UserService(AuthConfigProperties authConfigProperties, ApplicationEventPublisher applicationEventPublisher) {
        this.authConfigProperties = authConfigProperties;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        CustomOAuth2User customOAuth2User = fromGitHubUser(user, authConfigProperties.getGithubAliasAttribute(), authConfigProperties.getGithubNameAttribute());
        applicationEventPublisher.publishEvent(new OAuth2LoginEvent(customOAuth2User));
        return customOAuth2User;
    }

    /**
     * Factory method to create a custom user based on github oauth data.
     *
     * @param user           retrieved user
     * @param aliasAttribute attribute for the alias (login)
     * @param nameAttribute  attribute for the name
     * @return custom user
     */
    public static CustomOAuth2User fromGitHubUser(@NonNull final OAuth2User user,
                                                  @Nullable final String aliasAttribute,
                                                  @Nullable final String nameAttribute
    ) {
        var externalId = "";
        if (StringUtils.hasLength(nameAttribute)) {
            final var id = user.getAttribute("id");
            if (id == null) {
                externalId = "";
            } else {
                externalId = String.valueOf(id);
            }
        }

        var name = "";
        if (StringUtils.hasLength(nameAttribute)) {
            Object val = user.getAttribute(nameAttribute);
            if (val == null) {
                Object login = Objects.requireNonNull(user.getAttribute(aliasAttribute));
                name = String.valueOf(login);
            } else {
                name = String.valueOf(val);
            }
        }

        var alias = "";
        if (StringUtils.hasLength(aliasAttribute)) {
            alias = Optional.ofNullable((String) user.getAttribute(aliasAttribute)).orElse("");
        } else {
            alias = "";
        }

        return new CustomOAuth2User(
                externalId,
                alias,
                name,
                user.getAttributes(),
                user.getAuthorities(),
                user.getAttribute("avatar_url"),
                "github");
    }

}
