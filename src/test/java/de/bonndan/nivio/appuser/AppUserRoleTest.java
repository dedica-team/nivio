package de.bonndan.nivio.appuser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class AppUserRoleTest {

    @Test
    void valueOf() {

        assertThat(AppUserRole.valueOf("ADMIN"), is(notNullValue()));
        assertThat(AppUserRole.valueOf("USER"), is(notNullValue()));

    }
}