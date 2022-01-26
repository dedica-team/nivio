package de.bonndan.nivio.appuser;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserService implements UserDetailsService {

    private static final String USER_NOT_FOUND = "User with external id %s not found.";
    private final AppUserRepository appUserRepository;
    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String externalId) throws UsernameNotFoundException {
        return appUserRepository.findByExternalId(externalId)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, externalId)));
    }

}
