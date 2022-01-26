package de.bonndan.nivio.appuser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUserTest {

    AppUser appUser = new AppUser();

    @Test
    void isEnabled() {
        // when
        appUser.setEnabled(true);
        // then
        assertEquals(true, appUser.getEnabled());
    }

    @Test
    void getId() {
        // when
        appUser.setId(1L);
        // then
        assertEquals(1L, appUser.getId());
    }

    @Test
    void getName() {
        // when
        appUser.setName("name");
        // then
        assertEquals("name", appUser.getName());
    }

    @Test
    void getAlias() {
        // when
        appUser.setAlias("alias");
        // then
        assertEquals("alias", appUser.getAlias());
    }

    @Test
    void getEmail() {
        // when
        appUser.setEmail("email");
        // then
        assertEquals("email", appUser.getEmail());
    }

    @Test
    void getAvatarUrl() {
        // when
        appUser.setAvatarUrl("avatarUrl");
        // then
        assertEquals("avatarUrl", appUser.getAvatarUrl());
    }

    @Test
    void getAppUserRole() {
        // when
        appUser.setAppUserRole(AppUserRole.USER);
        // then
        assertEquals(AppUserRole.USER, appUser.getAppUserRole());
    }

    @Test
    void getExternalId() {
        // when
        appUser.setExternalId("123");
        // then
        assertEquals("123", appUser.getExternalId());
    }

    @Test
    void getIdp() {
        // when
        appUser.setIdp("github");
        // then
        assertEquals("github", appUser.getIdp());
    }

    @Test
    void getLocked() {
        // when
        appUser.setLocked(false);
        // then
        assertEquals(false, appUser.getLocked());
    }

    @Test
    void getEnabled() {
        // when
        appUser.setEnabled(true);
        // then
        assertEquals(true, appUser.getEnabled());
    }

    @Test
    void setId() {
        // given
        Long id = 1L;
        // when
        appUser.setId(id);
        // then
        assertEquals(id, appUser.getId());
    }

    @Test
    void setName() {
        // given
        String name = "name";
        // when
        appUser.setName(name);
        // then
        assertEquals(name, appUser.getName());
    }

    @Test
    void setAlias() {
        // given
        String alias = "login";
        // when
        appUser.setAlias(alias);
        // then
        assertEquals(alias, appUser.getAlias());
    }

    @Test
    void setEmail() {
        // given
        String email = "email";
        // when
        appUser.setEmail(email);
        // then
        assertEquals(email, appUser.getEmail());
    }

    @Test
    void setAvatarUrl() {
        // given
        String avatarUrl = "avatarUrl";
        // when
        appUser.setAvatarUrl(avatarUrl);
        // then
        assertEquals(avatarUrl, appUser.getAvatarUrl());
    }

    @Test
    void setAppUserRole() {
        // when
        appUser.setAppUserRole(AppUserRole.USER);
        // then
        assertEquals(AppUserRole.USER, appUser.getAppUserRole());
    }

    @Test
    void setLocked() {
        // given
        Boolean locked = false;
        // when
        appUser.setLocked(locked);
        // then
        assertEquals(locked, appUser.getLocked());
    }

    @Test
    void setEnabled() {
        // given
        Boolean enabled = true;
        // when
        appUser.setEnabled(enabled);
        // then
        assertEquals(enabled, appUser.getEnabled());
    }

    @Test
    void setExternalId() {
        // given
        String externalId = "123";
        // when
        appUser.setExternalId(externalId);
        // then
        assertEquals(externalId, appUser.getExternalId());
    }

    @Test
    void setIdp() {
        // given
        String idp = "github";
        // when
        appUser.setIdp(idp);
        // then
        assertEquals(idp, appUser.getIdp());
    }

}
