package de.bonndan.nivio.appuser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppUserTest {

    AppUser appUser = new AppUser();

    @Test
    void isEnabled() {
        appUser.setEnabled(true);
        assertEquals(true, appUser.getEnabled());
    }

    @Test
    void getId() {
        appUser.setId(1L);
        assertEquals(1L, appUser.getId());
    }

    @Test
    void getName() {
        appUser.setName("name");
        assertEquals("name", appUser.getName());
    }

    @Test
    void getAlias() {
        appUser.setAlias("alias");
        assertEquals("alias", appUser.getAlias());
    }

    @Test
    void getEmail() {
        appUser.setEmail("email");
        assertEquals("email", appUser.getEmail());
    }

    @Test
    void getAvatarUrl() {
        appUser.setAvatarUrl("avatarUrl");
        assertEquals("avatarUrl", appUser.getAvatarUrl());
    }

    @Test
    void getAppUserRole() {
        appUser.setAppUserRole(AppUserRole.USER);
        assertEquals(AppUserRole.USER, appUser.getAppUserRole());
    }

    @Test
    void getExternalId() {
        appUser.setExternalId("123");
        assertEquals("123", appUser.getExternalId());
    }

    @Test
    void getIdp() {
        appUser.setIdp("github");
        assertEquals("github", appUser.getIdp());
    }

    @Test
    void getLocked() {
        appUser.setLocked(false);
        assertEquals(false, appUser.getLocked());
    }

    @Test
    void getEnabled() {
        appUser.setEnabled(true);
        assertEquals(true, appUser.getEnabled());
    }

    @Test
    void setId() {
        Long id = 1L;
        appUser.setId(id);
        assertEquals(id, appUser.getId());
    }

    @Test
    void setName() {
        String name = "name";
        appUser.setName(name);
        assertEquals(name, appUser.getName());
    }

    @Test
    void setAlias() {
        String alias = "login";
        appUser.setAlias(alias);
        assertEquals(alias, appUser.getAlias());
    }

    @Test
    void setEmail() {
        String email = "email";
        appUser.setEmail(email);
        assertEquals(email, appUser.getEmail());
    }

    @Test
    void setAvatarUrl() {
        String avatarUrl = "avatarUrl";
        appUser.setAvatarUrl(avatarUrl);
        assertEquals(avatarUrl, appUser.getAvatarUrl());
    }

    @Test
    void setAppUserRole() {
        appUser.setAppUserRole(AppUserRole.USER);
        assertEquals(AppUserRole.USER, appUser.getAppUserRole());
    }

    @Test
    void setLocked() {
        Boolean locked = false;
        appUser.setLocked(locked);
        assertEquals(locked, appUser.getLocked());
    }

    @Test
    void setEnabled() {
        Boolean enabled = true;
        appUser.setEnabled(enabled);
        assertEquals(enabled, appUser.getEnabled());
    }

    @Test
    void setExternalId() {
        String externalId = "123";
        appUser.setExternalId(externalId);
        assertEquals(externalId, appUser.getExternalId());
    }

    @Test
    void setIdp() {
        String idp = "github";
        appUser.setIdp(idp);
        assertEquals(idp, appUser.getIdp());
    }

    @Test
    void getPassword() { assertNull(appUser.getPassword()); }

    @Test
    void getUsername() { assertNull(appUser.getUsername()); }

    @Test
    void isAccountNonExpired() { assertTrue(appUser.isAccountNonExpired()); }

    @Test
    void isAccountNonLocked() {
        Boolean locked = false;
        appUser.setLocked(locked);
        assertEquals(!locked, appUser.isAccountNonLocked()); }

    @Test
    void isCredentialsNonExpired() { assertTrue(appUser.isCredentialsNonExpired()); }

    @Test
    void testIsEnabled() { assertTrue(appUser.isEnabled()); }
}