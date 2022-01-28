package de.bonndan.nivio.appuser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void findByExternalId() {

        // given
        AppUser appUser = new AppUser();
        appUser.setExternalId("100");
        appUser.setAlias("login");
        appUser.setAppUserRole(AppUserRole.USER);
        appUser.setIdProvider("github");
        appUserRepository.save(appUser);

        // when
        final Optional<AppUser> fetchedAppUser = appUserRepository.findByExternalId(appUser.getExternalId());

        // then
        assertNotNull(appUser);

        assertThat(fetchedAppUser)
                .hasValueSatisfying(fetched -> {
                    assertThat(fetched.getExternalId()).isNotNull().isEqualTo(appUser.getExternalId());
                    assertThat(fetched.getAlias()).isNotNull().isEqualTo(appUser.getAlias());
                    assertThat(fetched.getId()).isNotNull();
                    assertThat(fetched.getIdProvider()).isNotNull().isEqualTo(appUser.getIdProvider());
                    assertThat(fetched.getAppUserRole()).isNotNull().isEqualTo(appUser.getAppUserRole());

                });
    }

}
