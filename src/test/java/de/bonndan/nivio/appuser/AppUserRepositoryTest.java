package de.bonndan.nivio.appuser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

        assertThat(fetchedAppUser)
                .hasValueSatisfying(fetched -> {
                    assertThat(fetched.getExternalId()).isNotNull().isEqualTo(appUser.get().getExternalId());
                    assertThat(fetched.getAlias()).isNotNull().isEqualTo(appUser.get().getAlias());
                    assertThat(fetched.getId()).isNotNull().isEqualTo(appUser.get().getId());
                    assertThat(fetched.getIdp()).isNotNull().isEqualTo(appUser.get().getIdp());
                    assertThat(fetched.getAppUserRole()).isNotNull().isEqualTo(appUser.get().getAppUserRole());

                });
    }

}