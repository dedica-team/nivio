package de.bonndan.nivio.appuser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppUserRoleTest {

    @Test
    void valueOf() {

        // when
        AppUserRole admin = AppUserRole.ADMIN;
        AppUserRole user = AppUserRole.USER;

        // then
        assertEquals(AppUserRole.valueOf("ADMIN"), admin);
        assertEquals(AppUserRole.valueOf("USER"), user);
        // handling default value for null and random string
        assertEquals(AppUserRole.of("test"), user);
        assertEquals(AppUserRole.of(null), user);
    }

}
