package de.bonndan.nivio.security;

import de.bonndan.nivio.appuser.AppUser;
import de.bonndan.nivio.appuser.AppUserRepository;
import de.bonndan.nivio.appuser.AppUserRole;
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

import static io.swagger.v3.oas.integration.StringOpenApiConfigurationLoader.LOGGER;

/**
 * Service that ensures that {@link CustomOAuth2User} users are used in the application.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AppUserRepository appUserRepository;

    private final AuthConfigProperties authConfigProperties;

    public CustomOAuth2UserService(AppUserRepository appUserRepository, AuthConfigProperties authConfigProperties) {
        this.appUserRepository = appUserRepository;
        this.authConfigProperties = authConfigProperties;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        try {
            CustomOAuth2User customOAuth2User = fromGitHubUser(user, authConfigProperties.getGithubAliasAttribute(), authConfigProperties.getGithubNameAttribute());
            saveUser(customOAuth2User);
            return customOAuth2User;
        } catch (NullPointerException e) {
            throw new OAuth2AuthenticationException(String.format("Failed to create custom user: %s", e.getMessage()));
        }
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
        var id = "";
        if (StringUtils.hasLength(nameAttribute)) {
            id = String.valueOf(user.getAttribute("id") == null ? "" : user.getAttribute("id"));
        }

        var name = "";
        if (StringUtils.hasLength(nameAttribute)) {
            Object val = user.getAttribute(nameAttribute);
            if (val == null) {
                Object login = Objects.requireNonNull(user.getAttribute("login"));
                name = String.valueOf(login);
            } else {
                name = String.valueOf(val);
            }
        }

        return new CustomOAuth2User(
                id,
                StringUtils.hasLength(aliasAttribute) ? Optional.ofNullable((String) user.getAttribute(aliasAttribute)).orElse("") : "",
                name,
                user.getAttributes(),
                user.getAuthorities(),
                user.getAttribute("avatar_url"),
                "github");
    }


    private void saveUser(CustomOAuth2User customOAuth2User) {
        Optional<AppUser> appUser = appUserRepository.findByEmail(customOAuth2User.getId());


        if (appUser.isEmpty()) {
            LOGGER.info("No user found, generating profile for {}", customOAuth2User.getId());
            AppUser newAppUser = new AppUser();
            newAppUser.setName(customOAuth2User.getName());
            newAppUser.setAlias(customOAuth2User.getAlias());
            newAppUser.setAvatarUrl(customOAuth2User.getAvatarUrl());
            newAppUser.setAppUserRole(AppUserRole.USER);
            newAppUser.setLocked(false);
            newAppUser.setEnabled(true);
            newAppUser.setExternalId(customOAuth2User.getId());
            newAppUser.setIdp(customOAuth2User.getIdp());

            appUserRepository.save(newAppUser);
        }

    }
}