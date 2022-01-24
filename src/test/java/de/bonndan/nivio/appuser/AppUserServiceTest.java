package de.bonndan.nivio.appuser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@DataJpaTest
class AppUserServiceTest {


    @Test
    void loadUserByUsername() {

        // given
        AppUser appUser = mock(AppUser.class);
        AppUserRepository appUserRepository = mock(AppUserRepository.class);
        AppUserService appUserService = new AppUserService(appUserRepository);

        // when
        doReturn(Optional.of(appUser)).when(appUserRepository).findByExternalId("123");
        UserDetails userDetails = appUserService.loadUserByUsername("123");

        // then
        assertThat(userDetails).isEqualTo(appUser);
        assertNull(userDetails.getUsername());
        assertNull(userDetails.getPassword());
        assertFalse(userDetails.isAccountNonExpired());
        assertFalse(userDetails.isAccountNonLocked());
        assertFalse(userDetails.isCredentialsNonExpired());

        assertThrows(UsernameNotFoundException.class, () -> {
            appUserService.loadUserByUsername("");
        });
    }

}