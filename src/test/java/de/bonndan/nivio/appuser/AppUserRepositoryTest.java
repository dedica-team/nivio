package de.bonndan.nivio.appuser;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void findByExternalId() {
        Optional<AppUser> appUser = Optional.of(new AppUser());
        appUser.get().setExternalId("100");
        appUser.get().setAlias("login");
        appUser.get().setAppUserRole(AppUserRole.USER);
        appUser.get().setId(1L);
        appUser.get().setIdp("github");

        appUser.ifPresent(user -> appUserRepository.save(user));
        final Optional<AppUser> fetchedAppUser = appUserRepository.findByExternalId(appUser.get().getExternalId());

        assertNotNull(appUser);
        assertNotNull(fetchedAppUser);
        assertEquals(fetchedAppUser.get().getExternalId(), appUser.get().getExternalId());

    }
}