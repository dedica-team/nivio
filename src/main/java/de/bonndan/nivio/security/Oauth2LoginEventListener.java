package de.bonndan.nivio.security;

import de.bonndan.nivio.appuser.AppUser;
import de.bonndan.nivio.appuser.AppUserRepository;
import de.bonndan.nivio.appuser.AppUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Oauth2LoginEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Oauth2LoginEventListener.class);

    private final AppUserRepository appUserRepository;

    public Oauth2LoginEventListener(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @EventListener(OAuth2LoginEvent.class)
    public void onLogin(final OAuth2LoginEvent oAuth2LoginEvent) {

        CustomOAuth2User customOAuth2User = oAuth2LoginEvent.getSource();
        Optional<AppUser> appUser = appUserRepository.findByExternalIdAndIdProvider(customOAuth2User.getExternalId(), customOAuth2User.getIdProvider());

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
            newAppUser.setIdProvider(customOAuth2User.getIdProvider());

            appUserRepository.save(newAppUser);
        }

    }

}
