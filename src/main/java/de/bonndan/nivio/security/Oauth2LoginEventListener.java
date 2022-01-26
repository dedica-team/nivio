package de.bonndan.nivio.security;

import de.bonndan.nivio.appuser.AppUser;
import de.bonndan.nivio.appuser.AppUserRepository;
import de.bonndan.nivio.appuser.AppUserRole;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static io.swagger.v3.oas.integration.StringOpenApiConfigurationLoader.LOGGER;

@Service
public class Oauth2LoginEventListener {

    private final AppUserRepository appUserRepository;

    public Oauth2LoginEventListener(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @EventListener(OAuth2LoginEvent.class)
    public void onLogin(final OAuth2LoginEvent oAuth2LoginEvent) {

        CustomOAuth2User customOAuth2User = oAuth2LoginEvent.getSource();
        Optional<AppUser> appUser = appUserRepository.findByExternalId(customOAuth2User.getExternalId());

        if (appUser.isEmpty()) {
            LOGGER.info("No user found, generating profile for {}", customOAuth2User.getExternalId());
            AppUser newAppUser = new AppUser();
            newAppUser.setName(customOAuth2User.getName());
            newAppUser.setAlias(customOAuth2User.getAlias());
            newAppUser.setAvatarUrl(customOAuth2User.getAvatarUrl());
            newAppUser.setAppUserRole(AppUserRole.USER);
            newAppUser.setLocked(false);
            newAppUser.setEnabled(true);
            newAppUser.setExternalId(customOAuth2User.getExternalId());
            newAppUser.setIdp(customOAuth2User.getIdp());

            appUserRepository.save(newAppUser);
        }

    }

}
